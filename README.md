#messagepax

Minimalistic MessagePack implementation for Java.

* Supports Java 5
* No dependencies. Just a few Java classes.
* Very basic interface but fast. No casts. Typesafe.
* Supports Boolean, Integer, Float, BigInteger, String, Byte array, List and Maps
* No support yet for extended type
* If you do not need a stream based API this one is perfect for your usecase. Very fast and low memory consumption. Perfect for embedded devices and Android

Based on the MessagePack object serialization specification of Sadayuki Furuhashi.
https://github.com/msgpack/msgpack/blob/master/spec.md

Build Status:

[![Build Status](https://buildhive.cloudbees.com/job/dedee/job/messagepax/badge/icon)](https://buildhive.cloudbees.com/job/dedee/job/messagepax/)
[![Build Status](https://travis-ci.org/dedee/messagepax.svg?branch=master)](https://travis-ci.org/dedee/messagepax)


## Examples

### Serializing data

    // Create the serializer instance
    MessagePaxSerializer s = new MessagePaxSerializer(buf);

    // Use the writeX method to add serialize data
    s.writeInteger(1);
    s.writeString("Hello World");

    // In the end you can access the buffer and used length to access serialized data
    System.out.println("Serialized to " + Utils.hex(s.getBuffer(), 0, s.getLength()));


### Deserializing data

    MessagePaxDeserializer s = new MessagePaxDeserializer(buf);
	Integer i = d.readInteger();
	String s = d.readString();


## Build

Use ANT build file 'build.xml' to build messagepax.jar.


## License

Copyright 2014 Dietrich Pfeifle - dedee.de

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

