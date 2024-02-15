package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    @Autowired
	public SocialMediaController(AccountService accountService, MessageService messageService) {
		this.accountService = accountService;
        this.messageService = messageService;
	}

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        Account acc = accountService.addAccount(account);
        if (acc != null) {
	        return ResponseEntity.ok(acc);
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(account);
	    }
        
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> opt = accountService.login(account);
        if (opt.isPresent()) {
            Account acc = opt.get();
	        return ResponseEntity.ok(acc);
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(account);
	    }
        
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (isValidMessage(message)) {
            Optional<Account> opt = accountService.findById(message.getPosted_by());
            if (opt.isPresent()) {
                Message m = messageService.addMessage(message);
                return ResponseEntity.ok(m);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user: User does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message: Check message_text length and posted_by");
        }

        }

    private boolean isValidMessage(Message message) {
        return message != null &&
               !message.getMessage_text().isBlank() &&
               message.getMessage_text().length() <= 255 &&
               message.getPosted_by() != null;
    }


    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getMessages() {
        return ResponseEntity.ok(messageService.getMessages());
    }

    @GetMapping("/messages/{message_id}")
    public ResponseEntity<Message> getMessage(@PathVariable("message_id") Integer id) {
        Optional<Message> opt = messageService.getMessage(id);
        if (opt.isPresent()) {
            Message m = opt.get();
            return ResponseEntity.ok(m);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getMessageByPostedBy(@PathVariable("account_id") Integer id) {
        Optional<Account> opt = accountService.findById(id);
        if (opt.isPresent()) {
            Optional<List<Message>> optm = messageService.getMessagesById(id);
            if (optm.isPresent()) {
                return (ResponseEntity.ok(optm.get()));
            } else {
                return ResponseEntity.ok(null);
            }
        } else {
            return ResponseEntity.ok(null);
        }
    }

    
    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable("message_id") Integer id) {
        Optional<Message> opt = messageService.getMessage(id);
        if (opt.isPresent()) {
            messageService.deleteMessage(id);
            return ResponseEntity.ok(1);
        } else {
            return ResponseEntity.ok(null);
        }
    }


    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateMessage(@PathVariable("message_id") Integer id, @RequestBody Message mess) {
        Optional<Message> opt = messageService.getMessage(id);
        if (opt.isPresent()) {
            Message message = opt.get();
            if (mess.getMessage_text().length() > 0 && mess.getMessage_text().length() < 255) {
                    message.setMessage_text(mess.getMessage_text());
                    messageService.addMessage(message);
                    return ResponseEntity.ok(1);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message");
                }
            } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message: Message does not exist");
        }

    }



    

}
