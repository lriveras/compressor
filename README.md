# Data Compression

## Usage
Package an executable jar.

To compress a file run `runnable.jar -compress -path/to/source -path/to/output`

To decompress a file run `runnable.jar -decompress -path/to/source -path/to/output`

## Compressor
The compressor uses the dictionary to check for repetitions in the latest 2^16 bytes of memory. If a repetition of more than 3 bytes is found, it is encoded in a 23 block, otherwise the literal byte is written in a 9 bit block.

#### Dictionary
The compressor uses a dictionary that hashes all the past combinations of three bytes with their index stored in them. The indexOf method for the dictionary receives a sequence of bytes and returns the largest repeated sequence in the dictionary by checking if the 3 bytes sequence is in the hash and later compares more bytes until it does not find larger repetitions. The add byte to index method in the dictionary will calculate and hash the last 3 bytes going backwards.

#### Block Writer
The BlockWriter function is to write contiguous 9 and/or 23 bits to a file. 
In java we can only write data in bytes so it is important that we use the writer to store data appropriately.

#### Space Complexity Analysis
The algorithm performs with constant space for an n > 2^16 where n is the ammount of bytes in the file. The space needed for the dictionary to store the address data is 2^16 * 4 (3 bytes for the hash and 1 for the buffer).


#### Time Complexity Analysis
A rough analysis was made for the performance of the algorithm meassuring memory and time efficiency to characterize performance. It can be seen that the algorithm shows a linear time complexity O(n).

#### Encode Performance

| File Size  | endTime - startTime (s)  | endTotalMemory - startTotalMemory (MB) |
| --- | --- | --- |
| 100KB | 1s | 39MB |
| 1MB | 6s | 21MB |
| 5MB | 48s | 26MB |
| 10MB | 97s | 33MB |
| 100MB | 1257s | 40MB |


| Original File Size  | Compressed File Size  |
| --- | --- |
| 100KB | 38KB |
| 1MB | 257KB |
| 5MB | 2MB |
| 10MB | 6MB |
| 100MB | 56MB |


## Decompressor
The decompressor uses a straigh forward approach where it reads from memory and starts writing bytes, the moment it finds an encoded block it will use the decoder dictionary to retrieve the past sequence of bytes and write it to the file as well.

#### Dictionary
The decompressor dictionary keeps the last 2^16 bytes in memory for future reference.

#### Block Reader
The block reader is in charge of reading 9 and 23 bits of memory at once depending of the type of the block. It keeps a buffer in memory to store unread bits. This is needed since Java only allows reading in bytes and encoding blocks are of odd lengths.

#### Space Complexity Analysis
The decoder performs with constant space complexity for n > 2^16 as it keeps only the latest 2^16 bytes in memory


#### Time Complexity Analysis
Performance tests also show a linear time complexity O(n).

#### Decode Performance

| File Size  | endTime - startTime (s)  | endTotalMemory - startTotalMemory (MB) |
| --- | --- | --- |
| 100KB | 0s | 0MB |
| 1MB | 5s | 7MB |
| 5MB | 35s | 4MB |
| 10MB | 75s | 31MB |
| 100MB | 805s | 20MB |


