/**
 *
 *  this packet handles creating problem instances and solving them.
 *
 *  In mapf scenario in left panel are solver controlls. They consists of avaliable solver choice box,
 *  solution items, actionDuration list, and buttons.
 *  User draws map, and sets agents. then he may choose solver he wants to use. Checkbox usecustombounds indicates
 *  if user wants to input his own range of length of solutions that will be looked for.
 *  After click solve button new instance of SolverProcess is generated, and is as SolverListItem added to solution list.
 *  SolverProcess creates another thread from where calls calls picatWrapper, and waits untill it ends and on end update
 *  SolverProcess status. And makes solution available to display and export.
 *
 * *
 * */


package mapfScenario.picat;