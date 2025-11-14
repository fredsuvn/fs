# _fs_: a lightweight, zero-dependency tool lib for java

## Overview:

![](logo.svg)

_fs_ is a lightweight, zero-dependency tool lib for java. It contains:

- annotations:
  * annotations:
  *
- app: Application framework with dependency injection support
- base: Fundamental data processing tools covering bytes, characters, encoding, dates, enums, etc.
- codec: Encoding/decoding utilities including Base64, cryptography, digest, and hexadecimal processing
- collect: Collection operation tools for arrays, lists, maps, sets, and stream processing
- io: Input/output handling with communication, file operations, and buffer management
- net: Network communication support for HTTP, TCP, and UDP protocols
- random: Random number generation services
- cache: Caching implementations with abstract and simple cache options
- concurrent: Concurrency processing tools that simplify Future operations
- object: Object conversion and data processing capabilities
- runtime: Runtime utilities including aspects, reflection, proxy, and invocation mechanisms
- third: Third-party integrations supporting ASM and Protocol Buffers

```java

```

## Clone

```shell
# clone and build
git clone -b master https://github.com/fredsuvn/fs.git
cd fs && gradle clean build
```

## Documents

- manual: [manual.adoc](docs/manual.adoc)
- javadoc: [javadoc](docs/javadoc/index.html)
- test-reports: [test-aggregate](docs/reports/test-aggregate/index.html)
- jacoco: [jacoco](docs/reports/jacoco/index.html)
- benchmark:
  * [jmh-visualizer](docs/reports/jmh/jmh-visualizer.html)
  * [results.json](docs/reports/jmh/results.json)

## Contact

* [github](https://github.com/fredsuvn)
* QQ group: 566185308

## License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html