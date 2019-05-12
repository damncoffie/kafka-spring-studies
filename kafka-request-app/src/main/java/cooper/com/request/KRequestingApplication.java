package cooper.com.request;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class KRequestingApplication {

    @Autowired
    KafkaAdmin kafkaAdmin;

    public static void main(String[] args) {
        SpringApplication.run(KRequestingApplication.class, args).close();
    }

    @Bean
    public ApplicationRunner runner(ReplyingKafkaTemplate<String, String, String> template) {
        return args -> {
            ProducerRecord<String, String> record = new ProducerRecord<>("kRequests", "foo");
            /* Starting with version 2.2, the template tries to detect the reply topic or partition from the configured
            reply container. If the container is configured to listen to a single topic or a single
            TopicPartitionInitialOffset, it is used to set the reply headers. If the container is configured otherwise,
            the user must set up the reply headers. In this case, an INFO log message is written during initialization.
            The following example uses KafkaHeaders.REPLY_TOPIC:
                        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "kReplies".getBytes()));
            */
            AdminClient client = AdminClient.create(kafkaAdmin.getConfig());
            Set<String> names = client.listTopics().names().get();
            boolean isDeleted = names.remove("__consumer_offsets");
            if (isDeleted) {
                client.deleteTopics(names);
                System.out.println("DONE");
            }

            RequestReplyFuture<String, String, String> replyFuture = template.sendAndReceive(record);
            SendResult<String, String> sendResult = replyFuture.getSendFuture().get();
            System.out.println("Sent ok: " + sendResult.getRecordMetadata());
            ConsumerRecord<String, String> consumerRecord = replyFuture.get();
            System.out.println("Return value: " + consumerRecord.value());
        };
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingTemplate(
            ProducerFactory<String, String> pf,
            ConcurrentMessageListenerContainer<String, String> repliesContainer) {

        ReplyingKafkaTemplate<String, String, String> template =
                new ReplyingKafkaTemplate<String, String, String>(pf, repliesContainer);
        return template;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {

        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer("replies");
        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
        repliesContainer.setAutoStartup(false);
        repliesContainer.setConcurrency(5);
        return repliesContainer;
    }

    @Bean
    public NewTopic kRequests() {
        return new NewTopic("kRequests", 10, (short) 1);
    }

    @Bean
    public NewTopic kReplies() {
        return new NewTopic("kReplies", 10, (short) 1);
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);
        kafkaAdmin.setFatalIfBrokerNotAvailable(true);
        return kafkaAdmin;
    }
}
