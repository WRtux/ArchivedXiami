# ArchivedXiami

Very few data of departed Xiami Music, archived for memorial purpose.  
已然逝去的虾米音乐之沧海一粟。

## Archived data

> The death of Xiami Music was confirmed at around 15:35:30, Feb 7, 2021 (UTC+8). All capture work has stopped.  
> Raw data processing is still in progress and will be uploaded in the following days.

Data are provided as PPMd compressed files in the release page and `/archive/` (partial).  
**For some reasons, old versions of data are not available now.**

* `pool.json`: data pool for detailed artists, albums, songs and comments data.
* `genre.json`: archive of the main pages in the genre and style section.
* `collect.json`: detailed data of some popular collections.

Tools may be needed to decompress and browse some huge JSON files.

## Source code

The source files used in different stages are in `/source/`.

* `webapi/`: scripts to capture data in a browser (unusable now).
* `aftermath/`: batches to download unprocessed HTML and media files, even after Feb 5 (only `media.bat` usable).
* `parser/`: tools to extract data from HTML and process all captured data.

New project [FrozenXiami](https://github.com/WRtux/FrozenXiami) has been launched to provide a browsing UI similar to Xiami.
