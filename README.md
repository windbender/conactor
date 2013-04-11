conactor
========

my attempt at conway's game of life within akka (java for now )

FWIW I TOTALLY recognize that this is hardly the most efficient way to run GOL.  The point here was that I wanted a way to start learning actors and akka in specific.

Things I've learned
========================
* since every messsage is a class, java is VERY TEDIOUS since every class is it's own file.  Of for the case class of scala!

* synchronizing completion of multiple tasks is not straight forward, and should be avoided.  In my case messages arriving "too early" were pushed back into the mailbox in ConwayCellACtor.


Next Steps
========================

* Remote Actors FTW,  ok that get's even less efficient ( see point above ), but it would be a way to learn about remote actors.
** if you do this, make sure the split the grid so that the least number of messages travel inter-JVM.

* have StateReportActor send pretty pictures to something like Processing for graphical display

* a quick CLI interface to specify parameters.

* redo it in scala for comparison's sake.

xxxx
