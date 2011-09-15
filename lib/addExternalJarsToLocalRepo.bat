:: Simple script to resolve initial importing issues with the used 3rd party
:: libraries.
 
@ECHO OFF
CLS
ECHO Adding required 3rd party libraries to local repository...
ECHO.

FOR %%F IN (pherd mbox2) DO (
	ECHO Adding %%F.jar
	mvn install:install-file -DgroupId=org.graphstream ^
	 -DartifactId=%%F  ^
	 -Dversion=1.0 ^
	 -Dfile=%%F.jar ^
	 -Dpackaging=jar ^
	 -DgeneratePom=true
)
