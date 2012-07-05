#!/bin/sh

path=/home/hduser/hadoop

scp $2 $1:$path

ssh $1 "$path/mrts_rstart.sh $2 $3"

