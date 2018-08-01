#Code written by team G19

from guizero import App, Text, TextBox, PushButton, Box, info
import time
from time import sleep
import servo
import tkinter as tk
import nfc_subprocess
import json


#requests
import requests
from requests.auth import HTTPBasicAuth

#PASSWORD (temporary)
global password
password = 1234

#nfc password
global nfc_password
nfc_password = 'hello1234'

#timer, timeout in seconds
global timeout_val
timeout_val = 20.0

#door open sleep time in seconds
global door_sleep
door_sleep = 5

#create numpad when click code button
def onclick_code():
    del_main()
    global input_text
    global numpad
    
    txt_sz = 25 #button text size
    num_padx = 55 # x axis padding for numbers
    input_text = TextBox(main_box, width = 40)
    numpad = Box(main_box, layout = "grid")
    #cols should have similar padding for numbers
    #col 1
    box_7 = PushButton(numpad, grid = [0,0], text = "7",padx = num_padx, pady = 15, command = onclick_7)
    box_4 = PushButton(numpad, grid = [0,1], text = "4",padx = num_padx, pady = 15, command = onclick_4)
    box_1 = PushButton(numpad, grid = [0,2], text = "1",padx = num_padx, pady = 15, command = onclick_1)
    box_clear = PushButton(numpad, grid = [0,3], text = "CLEAR",padx = 12, pady = 15, command = onclick_del)
    box_7.text_size = txt_sz
    box_4.text_size = txt_sz
    box_1.text_size = txt_sz
    box_clear.text_size = txt_sz
    
    box_8 = PushButton(numpad, grid = [1,0], text = "8",padx = num_padx, pady = 15, command = onclick_8)
    box_5 = PushButton(numpad, grid = [1,1], text = "5",padx = num_padx, pady = 15, command = onclick_5)
    box_2 = PushButton(numpad, grid = [1,2], text = "2",padx = num_padx, pady = 15, command = onclick_2)
    box_0 = PushButton(numpad, grid = [1,3], text = "0",padx = num_padx, pady = 15, command = onclick_0)
    box_8.text_size = txt_sz
    box_5.text_size = txt_sz
    box_2.text_size = txt_sz
    box_0.text_size = txt_sz

    box_9 = PushButton(numpad, grid = [2,0], text = "9",padx = num_padx, pady = 15, command = onclick_9)
    box_6 = PushButton(numpad, grid = [2,1], text = "6",padx = num_padx, pady = 15, command = onclick_6)
    box_3 = PushButton(numpad, grid = [2,2], text = "3",padx = num_padx, pady = 15, command = onclick_3)
    box_enter = PushButton(numpad, grid = [2,3], text = "ENTER", pady = 15, command = onclick_enter)
    box_9.text_size = txt_sz
    box_6.text_size = txt_sz
    box_3.text_size = txt_sz
    box_enter.text_size = txt_sz

#numpad buttns
def onclick_1():
    input_text.append("1")
def onclick_2():
    input_text.append("2")
def onclick_3():
    input_text.append("3")
def onclick_4():
    input_text.append("4") 
def onclick_5():
    input_text.append("5")
def onclick_6():
    input_text.append("6")
def onclick_7():
    input_text.append("7")
def onclick_8():
    input_text.append("8")
def onclick_9():
    input_text.append("9")
def onclick_0():
    input_text.append("0")
def onclick_del():
    input_text.clear()

#on click enter for numpad    
def onclick_enter():
    global input_text
    global numpad
    global main_box
    enter_text = int (input_text.get())
    #do something with the password
    if(enter_text == password):
        open_door()
        time.sleep(5)
        #wait for lever sensor
        servo.wait_for_lever()
        #sleep(20)
        close_door()
    
    #example, you could probably add some encryption to this too by calling another file
    f = open("input.txt","w+")
    f.write(str (enter_text))
    f.close()
    #example end
    main_box.destroy()
    show_start()
    nfc_subprocess.stop_stream()

def open_door():
    servo.SetAngle(0)

def close_door():
    servo.SetAngle(180)

#on click nfc button enable nfc tapping
def onclick_nfc():
    global main_box
    #popup box for nfc
    info("NFC", "Please tap OK when ready, then tap your phone to the NFC box above.")
    del_main()    
    nfc_subprocess.nfc_beam_recv()
    #get nfc code
    file = open("nfccode.txt", "r")
    nfc_code_get = file.read()
    print(nfc_code_get)
    #check if nfc code is valid
    r = requests.get('https://hizhh.me/api/unlock/1',auth=HTTPBasicAuth(nfc_code_get,'sdf'),verify=False)
    if(r.text != "Unauthorized Access"):
        open_door()
        print("OPEN SESAME")
        time.sleep(5)
        servo.wait_for_lever()
        #time.sleep(20)
        close_door()
    file.close()
    file = open("nfccode.txt", "w+")
    file.close()
    main_box.destroy()
    nfc_subprocess.stop_stream()
    show_start()

#delete things in the main box
def del_main():
    global initial_box
    global welcome_message
    welcome_message.destroy()
    initial_box.destroy()

#make main menu with enter code and nfc button
def make_main():
    global main_box
    global welcome_message
    global initial_box
    main_box = Box(app,layout = "auto")
    welcome_message = Text(main_box, text="Choose Method of Entry", size = 20)
    #box to hold 2 buttons
    initial_box = Box(main_box, layout = "grid")
    enter_code = PushButton(initial_box, grid = [0,0], text = "Enter Code", command = onclick_code, padx = 30, pady = 30)
    enter_code.text_size = 20
    nfc = PushButton(initial_box, grid = [0,1], text = "NFC", command = onclick_nfc, padx = 77, pady = 30)
    nfc.text_size = 20

#when clicking start button start stream
def onclick_start():
    nfc_subprocess.start_stream()
    make_main()
    start_button.hide()
    timeout_en.value = 1
    start_time.value = time.time()

def show_start():
    start_button.show()


# Action you would like to perform
def counter():
    if(int(timeout_en.value) == 1 and time.time() - float(start_time.value) > timeout_val):
        timeout_en.value = 0
        main_box.destroy()
        show_start()
        nfc_subprocess.stop_stream()

#main code loop for GUI

app = App(title="GUI")
app.tk.overrideredirect(True) #fullscreen mode
app.tk.overrideredirect(False)
app.tk.attributes('-fullscreen',True)
app.tk.config(cursor="none")

start_button = PushButton(app, text = "Start", command = onclick_start, padx = 50, pady = 50)
start_button.text_size = 20

timeout_en = Text(app, text="0")
timeout_en.hide()

start_time = Text(app, text="0")
start_time.hide()

global my_counter
timeout_en.repeat(100, counter)  # Schedule call to counter() every 1000ms

#put all app things before this app.display()
app.display()
