# Data Compression

## Usage
Package an executable jar.

To compress a file run `runnable.jar -compress -path/to/source -path/to/output`

To decompress a file run `runnable.jar -decompress -path/to/source -path/to/output`

## Compressor
The compressor uses the dictionary to check for repetitions in the latest 2^16 bytes of memory. If a repetition of more than 3 bytes is found, it is encoded in a 23 block, otherwise the literal byte is written in a 9 bit block.

#### Dictionary
The compressor uses a dictionary that hashes all the past combinations with their index stored in them. The indexOf method for the dictionary is O(1) given all repetitions are calculated and stored when a new character is added to the dictionary. The add byte to index method in the dictionary will calculate and hash all possible combinations of characters from 3 to 66 bytes going backwards. While adding a byte, if the combinations with the maximum ammout of bytes to be hashed is contained already, it can be infered that every other smaller combination is also contained therefore we do not iterate over them to index them again, instead we hash (in another map) the index of the first repetition, and as a value we store the index to the current repetition so we can check for it as well.

#### Block Writer
The BlockWriter function is to write contiguous 9 and/or 23 bits to a file. 
In java we can only write data in bytes so it is important that we use the writer to store data appropriately.

#### Space vs Time Performance Trade-offs
It is assumed there are no constrains in memory space for this problem, as it was required to make the algorithm as fast as possible. Not managing the deletions in the dictionary will make it as fast as it can be. A cap for the dictionary was also implemented, but its disabled by default, the cap will ensure after an arbitrary fixed point (specified in the constants) indices are deleted. Increasing the size of the dictionary will make the algorithm faster as it has to deal less with recalculation of previous repetitions.


#### Performance Analysis

A rough analysis was made for the performance of the algorithm meassuring memory and time efficiency to characterize performance. It can be seen that the algorithm shows a linear time complexity O(n).

#### Encode Performance

| File Size  | endTime - startTime (s)  | endTotalMemory - startTotalMemory (MB) |
| --- | --- | --- |
| 145KB | 6s | 360MB |
| 6MB | 35s | 399MB |
| 12MB | 53s | 548MB |
| 116MB | 508s | 1122MB |


| Original File Size  | Compressed File Size  |
| --- | --- |
| 145KB | 22KB |
| 6MB | 354KB |
| 12MB | 693KB |
| 116MB | 7MB |


## Decompressor
The decompressor uses a straigh forward approach where it reads from memory and starts writing bytes, the moment it finds an encoded block it will use the decoder dictionary to retrieve the past sequence of bytes and write it to the file as well.

#### Dictionary
The decompressor dictionary keeps the last 2^16 bytes in memory for future reference.

#### Block Reader
The block reader is in charge of reading 9 and 23 bits of memory at once depending of the type of the block. It keeps a buffer in memory to store unread bits. This is needed since Java only allows reading in bytes and encoding blocks are of odd lengths.

#### Space vs Time Performance Trade-offs
The Decompressor has no interesting trade-offs in terms of memory and time performance, as we always will need in memory a constant 2^16 bytes in memory and access time for this will always be O(1).


#### Performance Analysis
Performance tests also show a linear time complexity O(n) and contant space complexity O(1)

#### Decode Performance

| File Size  | endTime - startTime (s)  | endTotalMemory - startTotalMemory (MB) |
| --- | --- | --- |
| 145KB | 0s | 0MB |
| 6MB | 12s | 26MB |
| 12MB | 25s | 11MB |
| 116MB | 255s | 10MB |


