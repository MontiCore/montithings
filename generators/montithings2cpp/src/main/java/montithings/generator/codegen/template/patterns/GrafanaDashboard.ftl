<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("panels", "title")}
{
  \"annotations\": {
    \"list\": [
      {
        \"builtIn\": 1,
        \"datasource\": {
          \"type\": \"grafana\",
          \"uid\": \"-- Grafana --\"
        },
        \"enable\": true,
        \"hide\": true,
        \"iconColor\": \"rgba(0, 211, 255, 1)\",
        \"name\": \"Annotations & Alerts\",
        \"target\": {
          \"limit\": 100,
          \"matchAny\": false,
          \"tags\": [],
          \"type\": \"dashboard\"
        },
        \"type\": \"dashboard\"
      }
    ]
  },
  \"editable\": true,
  \"fiscalYearStartMonth\": 0,
  \"graphTooltip\": 0,
  \"id\": 13,
  \"links\": [],
  \"liveNow\": false,
  \"panels\": [
    <#list panels as panel>
    {
      \"datasource\": {
        \"type\": \"postgres\",
        \"uid\": \"eOSdhkbVk1234\"
      },
      \"fieldConfig\": {
        \"defaults\": {
          \"color\": {
            \"mode\": \"palette-classic\"
          },
          \"custom\": {
            \"axisCenteredZero\": false,
            \"axisColorMode\": \"text\",
            \"axisLabel\": \"\",
            \"axisPlacement\": \"auto\",
            \"barAlignment\": 0,
            \"drawStyle\": \"line\",
            \"fillOpacity\": 0,
            \"gradientMode\": \"none\",
            \"hideFrom\": {
              \"legend\": false,
              \"tooltip\": false,
              \"viz\": false
            },
            \"lineInterpolation\": \"linear\",
            \"lineWidth\": 1,
            \"pointSize\": 5,
            \"scaleDistribution\": {
              \"type\": \"linear\"
            },
            \"showPoints\": \"auto\",
            \"spanNulls\": false,
            \"stacking\": {
              \"group\": \"A\",
              \"mode\": \"none\"
            },
            \"thresholdsStyle\": {
              \"mode\": \"off\"
            }
          },
          \"mappings\": [],
          \"thresholds\": {
            \"mode\": \"absolute\",
            \"steps\": [
              {
                \"color\": \"green\",
                \"value\": null
              },
              {
                \"color\": \"red\",
                \"value\": 80
              }
            ]
          }
        },
        \"overrides\": []
      },
      \"gridPos\": {
        \"h\": 8,
        \"w\": 12,
        \"x\": ${panel.getX()},
        \"y\": ${panel.getY()}
      },
      \"id\": 2,
      \"options\": {
        \"legend\": {
          \"calcs\": [],
          \"displayMode\": \"list\",
          \"placement\": \"bottom\",
          \"showLegend\": true
        },
        \"tooltip\": {
          \"mode\": \"single\",
          \"sort\": \"none\"
        }
      },
      \"targets\": [
        {
          \"datasource\": {
            \"type\": \"postgres\",
            \"uid\": \"eOSdhkbVk\"
          },
          \"editorMode\": \"builder\",
          \"format\": \"table\",
          \"rawSql\": \"SELECT data, \\"timestamp\\" FROM ${panel.getSqlTable()} LIMIT 50 \",
          \"refId\": \"A\",
          \"sql\": {
            \"columns\": [
              {
                \"parameters\": [
                  {
                    \"name\": \"data\",
                    \"type\": \"functionParameter\"
                  }
                ],
                \"type\": \"function\"
              },
              {
                \"parameters\": [
                  {
                    \"name\": \"\\"timestamp\\"\",
                    \"type\": \"functionParameter\"
                  }
                ],
                \"type\": \"function\"
              }
            ],
            \"groupBy\": [
              {
                \"property\": {
                  \"type\": \"string\"
                },
                \"type\": \"groupBy\"
              }
            ],
            \"limit\": 50
          },
          \"table\": \"${panel.getTitle()}\"
        }
      ],
      \"title\": \"${panel.getSqlTable()}\",
      \"type\": \"timeseries\"
    }
    </#list>
  ],
  \"schemaVersion\": 37,
  \"style\": \"dark\",
  \"tags\": [],
  \"templating\": {
    \"list\": []
  },
  \"time\": {
    \"from\": \"now-6h\",
    \"to\": \"now\"
  },
  \"timepicker\": {},
  \"timezone\": \"\",
  \"title\": \"${title}\",
  \"uid\": \"lUkAhkb4k1234\",
  \"version\": 3,
  \"weekStart\": \"\"
}