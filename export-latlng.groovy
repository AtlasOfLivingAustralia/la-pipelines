@Grab('org.yaml:snakeyaml:1.26')
import org.yaml.snakeyaml.Yaml

if (this.args.length == 1) {
    Yaml yaml = new Yaml()
    def text = new File("/data/pipelines-data/${this.args[0]}/1/dwca-metrics.yml").text.replaceAll("[^a-zA-Z0-9: ]+", "")
    def dwcaMetrics = yaml.load(text)
    if (dwcaMetrics.archiveToErCountAttempted > 50000) {
        def proc = "./export-latlng-spark.sh ${this.args[0]}".execute()
        proc.consumeProcessOutput(System.out, System.err)
        proc.waitFor()
    } else {
        def proc = "./export-latlng.sh ${this.args[0]}".execute()
        proc.consumeProcessOutput(System.out, System.err)
        proc.waitFor()
    }
} else {
    println("Provide a data resource UID")
}