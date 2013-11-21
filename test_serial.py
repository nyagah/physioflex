import time
import serial

# connect to port
ser = serial.Serial('/dev/tty.usbserial-AD026C12', 57600)

# wait for self-reset at board to finish
time.sleep(2)

# configure to 'text' mode
ser.write('#ot')

time.sleep(2)

# read serial port and write to file
data_file = open('train_one_good.txt', 'w+')
cur_time = time.time()
end_time = time.time() + 30.0

data_file.write('Starting training... ignore first few samples\n')

while end_time > cur_time:
    data_file.write(ser.readline())
    cur_time = time.time()

data_file.write('\nDone training!')
data_file.close()
print 'Done!'

