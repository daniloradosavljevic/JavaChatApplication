# Quick Chat Commands Guide

- **Broadcast message to all users:**
  ```
  message_text
  ```

- **Private message:**
  ```
  /pm username message
  ```

- **Multicast message (to multiple users):**
  ```
  /mc user1 user2 ... message
  ```

---

## Chat Room Commands

- **Create a chat room:**
  ```
  /createroom roomName [invited_user]
  ```

- **List all chat rooms:**
  ```
  /rooms
  ```

- **Join a chat room:**
  ```
  /joinroom roomName
  ```

- **Send a message to a room:**
  ```
  /room roomName message
  ```

---

## Reply and Edit Functionality

- **Reply to a specific message**  
  (works for global and room messages; use the global message number as shown in the chat):
  ```
  /reply #message_number @username reply_text
  ```
  Example:
  ```
  /reply #2 @john That's a good point!
  ```
  - The reply will be visually marked and refer to the original message.

- **Edit your own message**  
  (works for global and room messages; you can only edit your own messages):
  ```
  /edit #message_number new_text
  ```
  Example:
  ```
  /edit #3 Updated message text
  ```
  - Edited messages are marked as `(edited #N)` where `N` is the global number of the edited message.

---

## Automatically Starting Server and 4 Clients

To automatically launch the server and 4 users in separate terminals, use the script:

```
python cmdchat.py
```

This will open dedicated terminal windows for the server and each client.

---