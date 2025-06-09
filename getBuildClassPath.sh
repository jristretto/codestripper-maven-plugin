#!/bin/sh

mvn dependency:build-classpath | grep -v '\[' > testclasspath.txt
