package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveClient incentiveClient;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveClient = incentiveClient;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public UserRecord findUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public boolean validateAndRecord(long senderId, long recipientId, float amount) {
        UserRecord sender = userRepository.findById(senderId);
        UserRecord recipient = userRepository.findById(recipientId);
        if (sender == null || recipient == null) {
            return false;
        }
        if (sender.getBalance() < amount) {
            return false;
        }
        // Fetch incentive from external API
        float incentive = incentiveClient.fetchIncentiveAmount(new com.jpmc.midascore.foundation.Transaction(senderId, recipientId, amount));

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentive);
        userRepository.save(sender);
        userRepository.save(recipient);
        transactionRepository.save(new TransactionRecord(sender, recipient, amount, incentive));
        return true;
    }
}
