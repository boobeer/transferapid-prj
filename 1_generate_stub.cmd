@ECHO OFF

set appname=transferapid

echo ---------GENERATE THE PROJECT---------
call mvn -B archetype:generate  -DarchetypeGroupId=org.apache.maven.archetypes  -DgroupId=org.bbr.examples  -DartifactId=%appname%
