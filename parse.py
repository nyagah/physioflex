#!/usr/bin/python

import sys

filename = sys.argv[1]

fhandle = open(filename, 'r')
for line in fhandle.readlines():
    data = line.split('=')
    if len(data) > 1:
        points = data[1].split(',')
        if len(points) == 3:
            print ' '.join(points).strip()



