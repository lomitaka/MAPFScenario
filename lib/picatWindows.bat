::arg $1 

echo win ..\lib\picat_windows.exe -g %1 %2 >> log.txt
::#$1 is predicate to run example: mapf_simple("pr200515233115","out200515233115") 
::#$2 is location where to look for predicate example: /home/ivan/work/MAPFScenario/picat/picat_iface.pi
cd ..\workdir
..\lib\picat_windows.exe -g %1 "%2"
