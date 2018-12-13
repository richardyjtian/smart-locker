#code written by team G19

import subprocess
import time
def nfc_beam_recv():
    #subprocess.call(["python", "beam.py", "--device", "tty:S0", "recv", "print"])
    python3_command ="python beam.py --device tty:S0 recv print"
    p = subprocess.Popen(python3_command.split(), stdout=subprocess.PIPE)
    start_time = time.time()
    while(p.poll() == None and time.time() - start_time < 20):
        var = 1
    if(p.poll == None):
        p.kill()
    print("Done")

def start_stream():
    python3_command ="sudo ./start_stream.sh"
    subprocess.Popen(python3_command.split(), stdout=subprocess.PIPE)

def stop_stream():
    python3_command ="sudo ./stop_stream.sh"
    subprocess.Popen(python3_command.split(), stdout=subprocess.PIPE)
