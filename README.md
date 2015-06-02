# Tarantula
[![Build Status](https://travis-ci.org/inf295uci-2015/Tarantula.svg?branch=master)](https://travis-ci.org/inf295uci-2015/Tarantula)
[![Coverage Status](https://coveralls.io/repos/inf295uci-2015/Tarantula/badge.svg?branch=master)](https://coveralls.io/r/inf295uci-2015/Tarantula?branch=master)

Code relevant to the Tarantula fault-localization metrics computation

## Installation Instructions for Tacoco and Primitive-Hamcrest

* Install the mvn packages to your local repository.  
  * primitive-hamcrest
```
    git clone https://github.com/inf295uci-2015/primitive-hamcrest.git  
    cd primitive-hamcrest  
    mvn test # just to make sure that everything works  
    mvn install # installs this to your local repository  
```
  * tacoco
```
    git clone https://github.com/inf295uci-2015/tacoco.git  
    cd tacoco  
    mvn test # just to make sure that everything works  
    mvn install # installs this to your local repository  
``` 

* Include the following dependency for Tacoco in your pom.xml (if it is not already there).
```
    <dependency>
      <groupId>org.spideruci.tacoco</groupId>
      <artifactId>tacoco</artifactId>
      <version>0.1</version>
    </dependency>
```
