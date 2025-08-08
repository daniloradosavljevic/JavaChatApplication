# Quick Chat – Master Project

**This is a Java chat application developed as part of master's coursework at the Faculty of Engineering, University of Kragujevac.**  
The app supports GUI (JavaFX) and simple terminal usage.

---

## GUI Preview

<img width="1000" height="779" alt="image" src="https://github.com/user-attachments/assets/95ca403f-1457-47ac-ac19-97421e466ce9" />


---

## Terminal Chat Commands

When using the terminal version, here are the available commands:

- **Send message to all:**  
  `message_text`

- **Private message:**  
  `/pm username message`

- **Multicast to multiple users:**  
  `/mc user1 user2 ... message`

- **Create chat room:**  
  `/createroom roomName [invited_user]`

- **List chat rooms:**  
  `/rooms`

- **Join chat room:**  
  `/joinroom roomName`

- **Send message to a room:**  
  `/room roomName message`

- **Reply to a message:**  
  `/reply #message_number @username reply_text`

- **Edit your message:**  
  `/edit #message_number new_text`

---

## Auto-launch (Server + 4 Clients)

To start the server and 4 clients automatically (each in a separate terminal window):

```
python cmdchat.py
```

---
