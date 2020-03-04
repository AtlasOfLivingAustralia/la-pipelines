new File("/data/pipelines-data/").listFiles().each { f ->
    if (f.name.startsWith("dr") ) {
        try {
            new java.net.URL("https://collections.ala.org.au/ws/dataResource/" + f.name).text
            println("OK," + f.name)
        } catch (Exception) {
            println("FAIL," + f.name)
        }
    }
}