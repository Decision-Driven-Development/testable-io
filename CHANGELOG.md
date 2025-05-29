# Changelog

All notable changes to this project will be documented in this file. See [standard-version](https://github.com/conventional-changelog/standard-version) for commit guidelines.

### [0.3.1](https://github.com/Decision-Driven-Development/testable-io/compare/v0.3.0...v0.3.1) (2025-05-29)


### Features

* ability to extract delay value from the response ([afd860b](https://github.com/Decision-Driven-Development/testable-io/commit/afd860b0cde46779e37cc435ac17dd4871ec21aa)), closes [#14](https://github.com/Decision-Driven-Development/testable-io/issues/14)

## [0.3.0](https://github.com/Decision-Driven-Development/testable-io/compare/v0.2.1...v0.3.0) (2025-05-20)


### Features

* ability to get the current response from the sequence without advancing it ([e30db4b](https://github.com/Decision-Driven-Development/testable-io/commit/e30db4b072194c97c8c2714c5f7e9a964bc17b96))
* ability to set up delays for any stubbed response ([c140bb3](https://github.com/Decision-Driven-Development/testable-io/commit/c140bb32c52962aab5d06de5886c61fcb35ed0a1))
* ability to store converters for each stubbed endpoint ([b111d55](https://github.com/Decision-Driven-Development/testable-io/commit/b111d559e10ffbaa9187e22accbc7ebfc0fe95cb))
* basic interface for Stub Facade ([41b0c93](https://github.com/Decision-Driven-Development/testable-io/commit/41b0c93e17496c8e3b3e4449040fc43aaecb130c))
* generify the StubbedQuery class ([c83c6af](https://github.com/Decision-Driven-Development/testable-io/commit/c83c6af4713e6f75ae9f92292e539e73cb5c1052))
* implement the delay before the attempt of response type conversion ([3c844cb](https://github.com/Decision-Driven-Development/testable-io/commit/3c844cbe2d61308f22d0babd58d424e4b0492023))
* implemented getting active single responses and resetting stubs for specific source ([882fd2d](https://github.com/Decision-Driven-Development/testable-io/commit/882fd2d6a0b2bd4b80343333f457235e069153ad))
* implemented query-specific converters ([2712773](https://github.com/Decision-Driven-Development/testable-io/commit/2712773bda3a4c7cad51aa56e471f0f6df8ab992))
* reimplemented delayed response ([b6b1c9f](https://github.com/Decision-Driven-Development/testable-io/commit/b6b1c9fff2f82524a8886b5ef295d081cfe8aa39))
* reimplemented sequence of responses ([b99d937](https://github.com/Decision-Driven-Development/testable-io/commit/b99d937fb0cc8bc280255b96cf9598c7e1ab200f))
* reimplemented source-specific stubs ([d72acc4](https://github.com/Decision-Driven-Development/testable-io/commit/d72acc426f1f5430e2c282e72782ada9735ec29a))
* reimplemented throwing exception as a response ([8e827bb](https://github.com/Decision-Driven-Development/testable-io/commit/8e827bb2af89a2ebaed17b9824d768abab4e7845))

### [0.2.1](https://github.com/Decision-Driven-Development/testable-io/compare/v0.2.0...v0.2.1) (2025-05-13)


### Features

* ability to reset stubs for specific client ([e7ee31d](https://github.com/Decision-Driven-Development/testable-io/commit/e7ee31d8014db8f12eb51fc224e39ab9d7efd23b)), closes [#6](https://github.com/Decision-Driven-Development/testable-io/issues/6)


### Bug Fixes

* return default responses extended with specific client responses ([9ff9c89](https://github.com/Decision-Driven-Development/testable-io/commit/9ff9c89fbd604c3b8c7d84ea2d3faca26678ab7e)), closes [#7](https://github.com/Decision-Driven-Development/testable-io/issues/7)

## [0.2.0](https://github.com/Decision-Driven-Development/testable-io/compare/v0.1.1...v0.2.0) (2025-05-04)


### Features

* ability to assign stored Response to client Stub ([0715a23](https://github.com/Decision-Driven-Development/testable-io/commit/0715a230b48bbbaa8d79db2020fb8c9073a6f596)), closes [#2](https://github.com/Decision-Driven-Development/testable-io/issues/2)
* ability to assign stored Response to client Stub and specific Query ([741e230](https://github.com/Decision-Driven-Development/testable-io/commit/741e230ac7ac7b428335d8828e29e80ff4383aa6)), closes [#2](https://github.com/Decision-Driven-Development/testable-io/issues/2)
* ability to build the Stub record programmatically ([27761df](https://github.com/Decision-Driven-Development/testable-io/commit/27761df3592b1c7cbc30d17d51d90d655f11b7e5)), closes [#2](https://github.com/Decision-Driven-Development/testable-io/issues/2)
* ability to peek at current responses ([76f8359](https://github.com/Decision-Driven-Development/testable-io/commit/76f83597ef8870a1912890e34f402823cb3c8a31)), closes [#4](https://github.com/Decision-Driven-Development/testable-io/issues/4)
* ability to set up the Exception as the Response ([3419307](https://github.com/Decision-Driven-Development/testable-io/commit/34193071875686abe2d5056d10ee9feeb7cec002)), closes [#2](https://github.com/Decision-Driven-Development/testable-io/issues/2)
* add name to the Stub record ([19f2bbf](https://github.com/Decision-Driven-Development/testable-io/commit/19f2bbfb82ff810a5f7e3403130359b5ba373c10)), closes [#2](https://github.com/Decision-Driven-Development/testable-io/issues/2)
* store all the created responses in memory ([7b7a538](https://github.com/Decision-Driven-Development/testable-io/commit/7b7a5387eb6bfbc564f7d8cbe287b4f3a504f489)), closes [#2](https://github.com/Decision-Driven-Development/testable-io/issues/2)

### [0.1.1](https://github.com/Decision-Driven-Development/testable-io/compare/v0.1.0...v0.1.1) (2025-04-24)


### Features

* got rid of generic in GenericRequest ([406d309](https://github.com/Decision-Driven-Development/testable-io/commit/406d309510719865ccc174c8815f9d1ddbb6a641))
* got rid of generic in GenericResponse ([a90b06f](https://github.com/Decision-Driven-Development/testable-io/commit/a90b06fbf450db2513428ab5e203d7f1c5b007b7))

## 0.1.0 (2025-04-21)


### Features

* ability to configure responses ([2717786](https://github.com/Decision-Driven-Development/testable-io/commit/2717786e0699eabb23ca5bf050ab6d758d2cfcef))
* ability to create the real request out of generic request ([2a9574d](https://github.com/Decision-Driven-Development/testable-io/commit/2a9574da151656464e4e02bb458324e004f89747))
* ability to store different stubs for different clients ([5753c7c](https://github.com/Decision-Driven-Development/testable-io/commit/5753c7ca6b576994f5add1abd7eb0bf1f0208602))
* implemented some ConfiguredResponse functionality ([950519a](https://github.com/Decision-Driven-Development/testable-io/commit/950519af381974ce5b61f120ea9cd3e260114204))
* **stub:** naive implementations of generic request and response ([bec2cf1](https://github.com/Decision-Driven-Development/testable-io/commit/bec2cf134809b068554fd7404fedaff9feabf600))
