#!/bin/sh
cd ../
rm -rf ASmallLife/distr/asmalllife-0.5.zip
zip -r ASmallLife/distr/asmalllife-0.5.zip ASmallLife/bin/ ASmallLife/data/*.jpg ASmallLife/data/*.png ASmallLife/data/*.obj ASmallLife/data/*.md5mesh ASmallLife/data/*.md5anim ASmallLife/libs/ ASmallLife/README.md ASmallLife/start_win32.bat ASmallLife/start_linux.sh
