# BDTCR: Test Case Recommendation based on Balanced Distance of Test Targets

## Requirements
JDK==1.8;

JavaParser==3.15.7;

MySQL==8.0.19;

More requirements are list in the pom.xml file.


## Dataset
The test case corpus can be downloaded on the [website](https://drive.google.com/drive/folders/11_vGBKkPkapjDQVlbX7IrJXjrbBI8EqW)


## Baseline
NiCad-based: The implementation code of NiCad-based is in java/com/bdtcr/comparisons/nicadbased.
TestTenderer: The implementation code of NiCad-based is in java/com/bdtcr/comparisons/testtenderer.


## BDTCR
BDTCR consists of two modules, test case construction (TConstructer) and test case search (TSearcher).

### Test Case Construction (TConstructer)
The source code of the TConstructer module is in java/com/bdtcr/tconstructer.

### Test Case Search (TSearcher)
The source code of the TSearcher module is in java/com/bdtcr/tsearcher.
