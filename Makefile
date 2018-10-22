JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	MultiThreadedServer.java \
	NewClient.java \
	WorkerRunnable.java \
	Main.java 

default: classes

classes: $(CLASSES:.java=.class)

all:
	javac *.java

run:
	java Main

clean:
	$(RM) *.class
