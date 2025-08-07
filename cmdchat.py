import subprocess
import time
import os
import socket
import threading

bin_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "bin")
java_cmd = 'java -cp ".;../lib/kryonet-2.21-all.jar"'
server_port = 54555  # ili 54555, po potrebi

commands = [
    f'{java_cmd} rs.raf.pds.v4.z5.ChatServer {server_port}',
    f'{java_cmd} rs.raf.pds.v4.z5.ChatClient localhost {server_port} Korisnik1',
    f'{java_cmd} rs.raf.pds.v4.z5.ChatClient localhost {server_port} Korisnik2',
    f'{java_cmd} rs.raf.pds.v4.z5.ChatClient localhost {server_port} Korisnik3',
    f'{java_cmd} rs.raf.pds.v4.z5.ChatClient localhost {server_port} Korisnik4'
]

def open_cmd_in_new_window(cmd, cwd):
    subprocess.Popen(
        f'start cmd /k "{cmd}"',
        cwd=cwd,
        shell=True
    )

def wait_for_port(host, port, timeout=20):
    start_time = time.time()
    while True:
        try:
            with socket.create_connection((host, port), timeout=2):
                return
        except (OSError, ConnectionRefusedError):
            if time.time() - start_time > timeout:
                raise TimeoutError(f"Port {port} na {host} nije otvoren ni posle {timeout} sekundi.")
            time.sleep(0.5)

def start_server_and_clients():
    open_cmd_in_new_window(commands[0], bin_dir)
    print("Pokrenut server, cekam da port bude otvoren...")

    try:
        wait_for_port("localhost", server_port, timeout=20)
        print("Server je spreman! Pokrećem klijente...")
    except TimeoutError as e:
        print(str(e))
        return

    def start_client(cmd):
        open_cmd_in_new_window(cmd, bin_dir)

    for cmd in commands[1:]:
        t = threading.Thread(target=start_client, args=(cmd,))
        t.start()
        time.sleep(1)  

if __name__ == "__main__":
    start_server_and_clients()