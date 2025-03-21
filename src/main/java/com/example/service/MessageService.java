package com.example.service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public ResponseEntity<Message> createMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty() ||
                message.getMessageText().length() > 255 || message.getPostedBy() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String username = getUsernameByAccountId(message.getPostedBy());
        if (username == null || !accountRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    public String getUsernameByAccountId(Integer accountId) {
        Account account = accountRepository.findByAccountId(accountId);

        return account != null ? account.getUsername() : null;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public ResponseEntity<Message> getMessageById(Integer messageId) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            return ResponseEntity.ok(optionalMessage.get());
        } else {
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Object> deleteMessageById(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return ResponseEntity.ok().body("1");
        } else {
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Object> updateMessageText(Integer messageId, Message newMessageText) {
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
    
        if (optionalMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message not found");
        }
    
        Message message = optionalMessage.get();
    
        if (newMessageText.getMessageText() == null || newMessageText.getMessageText().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message text cannot be empty");
        }
    
        if (newMessageText.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message too long: it must have a length of at most 255 characters");
        }
    
        message.setMessageText(newMessageText.getMessageText());
        messageRepository.save(message);
    
        return ResponseEntity.ok().body("1"); // Assuming "1" represents the number of rows updated
    }

    public ResponseEntity<List<Message>> getMessagesByAccountId(Integer accountId) {
        List<Message> messages = messageRepository.findByPostedBy(accountId);

        return ResponseEntity.ok(messages);
    }
}
