#arg $1 
#echo linux ../lib/picat_linux -g $1 $2 >> log.log
echo running picatLinux.sh $1 $2

#$1 is predicate to run example: mapf_simple("pr200515233115","out200515233115") 
#$2 is location where to look for predicate example: /home/ivan/work/MAPFScenario/picat/picat_iface.pi
cd ../workdir
../lib/picat_linux -g $1 "$2"
