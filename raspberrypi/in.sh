#!/bin/bash

r="/sys/class/gpio"
declare -a last

while true
do
  elements=$(dir $r)
	for element in $elements
	do
		if [[ $element == "export" || $element == "unexport" || $element == "gpiochip0" ]]
		then
	#		echo "unwanted element: $element"
			one=1
		else
	#		echo "wanted element: $element"
			pin=$(echo $element | cut -c5-8)
			value=$(cat ${r}/gpio${pin}/value)
	#		echo "^^ is pin $pin"
			if [[ ${last[$pin]} == $value ]]
			then
	#			echo "nothing changed - value: $value"
				two=2
			else
				echo "%$pin,$value&"
			fi
			last[$pin]=$value
	#		echo ${last[@]}
		fi
	done
done
