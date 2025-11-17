# ![](logo.svg) _fs_: a lightweight, zero-dependency tool lib for java

## Overview

_fs_ is a lightweight, zero-dependency tool lib for java. It provides:

- Annotations that can be used for code analysis;
- Common utilities for bytes, chars, coding, date, enum, logging, math, number, thread, process, system, random,
  reflect, etc.;
- Extensions for functions and values;
- Common exceptions;
- I/O kits and interfaces;
- Cache kits;
- Operations for collections, maps, arrays, etc.;
- Operations for codec;
- Concurrent supports;
- Net kits for http, tcp, udp, etc.;
- Object parsing and conversion;
- Invocation kits, supporting reflection, method-handle and [asm](https://asm.ow2.io);
- Proxy and aspect, supporting JDK dynamic proxy and [asm](https://asm.ow2.io);
- Third-party supporting: [asm](https://asm.ow2.io), [protobuf](https://github.com/protocolbuffers/protobuf);

### Coverage: 100%

The success rate and coverage rate of unit testing for this library are both 100%. (coverage tool: jacoco)

### Samples:

- [Annotations](./fs-tests/src/main/java/internal/samples/AnnotationSample.java)
- [Simple Cache](./fs-tests/src/main/java/internal/samples/CacheSample.java)
- [Dependency Injection](./fs-tests/src/main/java/internal/samples/DISample.java)
- [Proxy and Aspect](./fs-tests/src/main/java/internal/samples/ProxyAndAspectSample.java)
- [Net](./fs-tests/src/main/java/internal/samples/NetSample.java)
- [Other samples](./fs-tests/src/main/java/internal/samples/OtherSamples.java)

## Clone

```shell
# clone and build
git clone -b master https://github.com/fredsuvn/fs.git
cd fs && gradle clean build
```

## Documents

- javadoc:
  * javadoc: [index.html](docs/javadoc/index.html)
- manuals:
  * develop manual: [dev-manual.adoc](docs/dev-manual.adoc)
- benchmarks:
  * visualizer: [jmh-visualizer.html](docs/jmh/jmh-visualizer.html)
  * json: [results.json](docs/jmh/results.json)

## Contact

* github: [https://github.com/fredsuvn](https://github.com/fredsuvn)
* QQ group: [566185308](https://qm.qq.com/q/wlkc2tmOaG)

## License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html