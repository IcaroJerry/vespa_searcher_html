# vespa.ai match map plugin
This project is a plugin for [vespa.ai](https://vespa.ai/) search engine that includes a new Searcher capable of ignore HTML tags in the searched text.

## Installation

```bash
mvn install package -f vespa-bypass-html-searcher/
```

On your search definition file, please include `bypass_tag` as a query command in your fields that are going to be bypassed. `bypass_tag` requires a highlight configuration (`bolding: on`).

```
field field_name type string {
    indexing: summary | index
    bolding: on
    query-command: bypass_tag
}
```

On your *services.xml* file, add the searcher (`com.escavador.prelude.searcher.BypassTagSearcher`) into a search chain:

```xml
<?xml version="1.0" encoding="utf-8" ?>
<services version="1.0">
    <admin version="2.0">
      <adminserver hostalias="node1" />
    </admin>

    <container version="1.0">
        <search>
          <chain id="default" inherits="vespa">
            <searcher id="com.escavador.prelude.searcher.BypassTagSearcher" />
          </chain>
        </search>
        <nodes>
          <node hostalias="node1" />
        </nodes>
    </container>
</services>
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[GPL v3](https://www.gnu.org/licenses/gpl-3.0.html)

## Authors and acknowledgement
This project has received contributions of code from amazing authors, please see the contributors page:

[Authors](https://github.com/escavador/vespa-php/graphs/contributors)
