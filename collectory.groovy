@Grab('mysql:mysql-connector-java:5.1.39')
@GrabConfig( systemClassLoader=true )

import groovy.sql.Sql
import groovy.json.JsonSlurper

def writer = new FileWriter("/tmp/datasets.csv")

def map = [:]

def sql = Sql.newInstance('jdbc:mysql://localhost/collectory?autoReconnect=true&connectTimeout=0&useUnicode=true&characterEncoding=UTF-8', "root", "password", "com.mysql.jdbc.Driver")
sql.query("select uid, name, connection_parameters from data_resource") {  rs ->
    while (rs.next()) {
        def connParams = rs.getString('connection_parameters')
        if(connParams) {
            def json
            try {
                json = new JsonSlurper().parseText(connParams)
            } catch (Exception e){}

            def protocol = json?.protocol?:'Not available'
            def count = map.getOrDefault(protocol, 0) +  1
            map.put(protocol, count)
            println(rs.getString('uid') + ',' + protocol);
            writer.write(rs.getString('uid') + ',' + protocol + "," + json?.url + '\n')
        }
    }
}
writer.flush()
writer.close()

println(map)






