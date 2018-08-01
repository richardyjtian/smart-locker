#code written by team G19

import RPi.GPIO as GPIO
from time import sleep

def SetAngle(angle):
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(36, GPIO.OUT)
        pwm=GPIO.PWM(36, 50)
        pwm.start(0)
        duty = angle / 18 + 2
        GPIO.output(36, True)
        pwm.ChangeDutyCycle(duty)
        sleep(1)
        GPIO.output(36, False)
        pwm.ChangeDutyCycle(0)
        pwm.stop()
        GPIO.cleanup()

def wait_for_lever():
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(32, GPIO.OUT)
        GPIO.output(32,1)
        GPIO.setup(40, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        while(not(GPIO.input(40))):
                var = 1
        sleep(2)
        GPIO.cleanup()
wait_for_lever()
