#!/bin/sh

path=~/Licenta/hadoop

scp $2 $1:$path

ssh $1 "$path/mrts_start.sh $2 $3"

