# Kratka uputstva za komande

- **Broadcast poruka svim korisnicima:**
  ```
  tekst_poruke
  ```

- **Privatna poruka:**
  ```
  /pm korisnicko_ime poruka
  ```

- **Multicast poruka (više korisnika):**
  ```
  /mc korisnik1 korisnik2 ... poruka
  ```

---

## Komande za chat sobe

- **Kreiranje sobe:**
  ```
  /createroom imeSobe [korisnik_pozvan]
  ```

- **Prikaz svih soba:**
  ```
  /rooms
  ```

- **Pridruži se sobi:**
  ```
  /joinroom imeSobe
  ```

- **Slanje poruke u sobu:**
  ```
  /room imeSobe poruka
  ```

---

## Automatsko pokretanje servera i 4 korisnika

Za automatsko pokretanje servera i 4 korisnika u posebnim terminalima koristi skriptu:

```
python cmdchat.py
```