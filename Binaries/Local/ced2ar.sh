#!/bin/bash
# now open a browser, after letting Tomcat spin up
echo "
============ CED2AR ===============

To exit CED2AR, hit CTRL-C in the Terminal.

Waiting 10s for Tomcat to start" 


URL='http://localhost:8080/ced2ar-web/search'
if which xdg-open > /dev/null
then
	(sleep 10; xdg-open $URL ) &
elif which gnome-open > /dev/null
then
  (sleep 10; gnome-open $URL ) &
fi

# find java
javabin=$(which java)
javaversion=`$javabin -version 2>&1 | head -1 | awk -F\" ' { print $2 } '`
if [[ "$javaversion" > "1.8" ]]
then
	echo "Default Java is 1.8.x. Looking for Java 1.7"
	javabin=
        for arg in $(cd /usr/java; ls -1d jr* jd* | sort -r)
	do 
		[[ -z $javabin ]] && [[ -x /usr/java/$arg/bin/java ]] && javabin=/usr/java/$arg/bin/java
		[[ -z $javabin ]] || javaversion=`$javabin -version 2>&1 | head -1 | awk -F\" ' { print $2 } '`
		[[ "$javaversion" > "1.8" ]] && javabin=
	done 
fi
if [[ -z $javabin ]]
then
	echo "Could not find a version of Java less than 1.8."
	echo "this app requires Java 1.7"
	echo "Please install Java 1.7 and come back"
	exit 2
fi

# start CED2AR
if which zenity > /dev/null
then
     $javabin -jar ced2ar.jar 2> ced2ar.log | zenity --text-info --title "CED2AR"
else
     $javabin -jar ced2ar.jar 2> ced2ar.log
fi

