#echo cygwin ..\lib\picat_windows.exe -g %1 %2 >> log.txt
echo running picatCygwin.sh $1 $2

#$1 is predicate to run example: mapf_simple("pr200515233115","out200515233115") 
#$2 is location where to look for predicate example: /home/ivan/work/MAPFScenario/picat/picat_iface.pi

cygpath=/cygdrive/`echo "$2" | sed "s/^[A-Z]/\L&/" | sed "s/://"`


cd ../workdir
../lib/picat_cygwin.exe -g $1 "$cygpath"
