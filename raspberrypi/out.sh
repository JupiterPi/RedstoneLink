#!/bin/bash

r="/sys/class/gpio"

while true
do
	read -p "cmd: " action pin value
#	echo $action
#	echo $pin
#	echo $value
	if [ $action == "export" ]
	then
#		echo "export"
		echo $pin > "$r/export"
		echo "exported pin $pin"
	elif [ $action == "setup" ]
	then
#		echo "setup"
		echo $value > "${r}/gpio${pin}/direction"
		echo "set up pin $pin for direction $value"
	elif [ $action == "out" ]
	then
#		echo "out"
		echo $value > "${r}/gpio${pin}/value"
		echo "output $value for pin $pin"
	elif [ $action == "unexport" ]
	then
		echo $pin > "${r}/unexport"
		echo "unexported pin $pin"
	elif [ $action == "exit" ]
	then
		echo "exiting"
		break
	else
		echo "unknown command"
	fi
done
