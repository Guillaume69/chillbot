#! /bin/sh

cd /app
./chillbot-srv-1/bin/chillbot-srv -Dconfig.file=./chillbot-srv-1/conf/prod.conf -Dhttp.port=9200 -Dplay.evolutions.db.default.autoApply=true -Dplay.evolutions.db.default.autoApplyDowns=true