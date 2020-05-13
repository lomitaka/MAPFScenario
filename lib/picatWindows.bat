echo "Bla" >> blas
echo "$1 $2 " >> blas
#arg $1 

# "/home/ivan/work/MAPFScenario/test/lib" "mapf_simple('/home/ivan/work/MAPFScenario/workdir/pr20-01-21-22-09-34.pi','/home/ivan/work/MAPFScenario/workdir/pr20-01-21-22-09-34.answ1')" "/home/ivan/work/MAPFScenario/picat/picat_iface.pi"

#./picat -g "run('/home/ivan/work/MAPFScenario/workdir/pr20-01-21-22-09-34.pi','/home/ivan/work/MAPFScenario/workdir/pr20-01-21-22-09-34.answ')" '/home/ivan/work/MAPFScenario/picat/mapf_simple.pi'

# @1 is predicate to run example: "run('/home/ivan/work/MAPFScenario/workdir/pr20-01-21-22-09-34.pi','/home/ivan/work/MAPFScenario/workdir/pr20-01-21-22-09-34.answ')" 
# @2 is location where to look for predicate example: '/home/ivan/work/MAPFScenario/picat/mapf_simple.pi'

picat_windows.exe -g @1 @2
