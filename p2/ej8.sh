echo CPU
for i in 250 500 750 1000 1250 1500 1750 200 2250 2500 2750 3000 3250 3500
do
	tail -l $i\u/cpu-vm2.txt | awk 	'{print $4}'
done
echo Monitor
forfor i in 250 500 750 1000 1250 1500 1750 200 2250 2500 2750 3000 3250 3500
do
	tail -l $i\u/monitor.txt | awk '{printf("%12s\t%12s\t%12s\n", $2, $3, $4)}'
done
