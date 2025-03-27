<template>
  <div>
    <div :id="chartId" style="height: 600px"></div>
  </div>
</template>

<script>
import { getEntitiesWithRelationship } from "@/api/patentSys/patents";
import echarts from "echarts";

export default {
  name: "EntityRelationshipGraph",
  props: {
    chartId: {
      type: String,
      default: "entityRelationshipGraph"
    },
    selectedK: {
      type: Number,
      default: 30
    }
  },
  watch: {
    selectedK() {
      this.initChart();
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getEntitiesWithRelationship().then(response => {
        let data = response.data.slice(0, this.selectedK);
        let nodes = new Map();
        let links = [];
        let categories = new Set();
        let relationTypes = new Set();

        const typeColors = {
          PRODUCT: '#1f77b4',
          METHOD: '#ff7f0e',
          STRUCTURE: '#2ca02c',
          EFFICACY: '#d62728',
          FIELD: '#9467bd',
          MATERIAL: '#8c564b'
        };
        const relationColors = {
          'CONSISTS-OF': '#59c4e6',
          'IMPROVES': '#edafda',
          'USED-FOR': '#93b7e3',
          'SUBCLASS-OF': '#a5e7f0',
          'EQUAL': '#cbb0e3'
        };

        data.forEach(item => {
          if (!nodes.has(item.entity1)) {
            nodes.set(item.entity1, {
              id: item.entity1,
              name: item.entity1,
              type: item.entity1Type,
              symbolSize: item.degree * 3,
              category: item.entity1Type,
              itemStyle: { color: typeColors[item.entity1Type] },
              draggable: true
            });
            categories.add(item.entity1Type);
          }
          if (!nodes.has(item.entity2)) {
            nodes.set(item.entity2, {
              id: item.entity2,
              name: item.entity2,
              type: item.entity2Type,
              symbolSize: 15,
              category: item.entity2Type,
              itemStyle: { color: typeColors[item.entity2Type] },
              draggable: true
            });
            categories.add(item.entity2Type);
          }
          links.push({ 
            source: item.entity1, 
            target: item.entity2, 
            name: item.relation, 
            lineStyle: { color: relationColors[item.relation] } 
          });
          relationTypes.add(item.relation);
        });

        const nodeArray = Array.from(nodes.values());
        const categoryArray = Array.from(categories).map(type => ({ 
          name: type, 
          itemStyle: { color: typeColors[type] } 
        }));
        const relationArray = Array.from(relationTypes).map(relation => ({
          name: relation,
          itemStyle: { color: relationColors[relation] }
        }));

        const myChart = this.$echarts.init(document.getElementById(this.chartId));
        const option = {
          title: {
            text: '实体关系图',
            left: 'center'
          },
          tooltip: {
            formatter: function (x) {
              if (x.dataType === 'edge') {
                return `${x.data.source} ${x.data.name} ${x.data.target}`;
              }
              return x.data.name;
            }
          },
          toolbox: {
            show: true,
            feature: {
              dataView: { show: true, readOnly: false },
              restore: { show: true },
              saveAsImage: { show: true }
            }
          },
          legend: [
            {
              data: categoryArray.map(category => category.name),
              left: 'left',
              orient: 'vertical'
            },
            {
              data: relationArray.map(relation => relation.name),
              left: 'right',
              orient: 'vertical'
            }
          ],
          series: [
            {
              type: 'graph',
              layout: 'force',
              data: nodeArray,
              links: links,
              categories: categoryArray,
              roam: true,
              label: {
                show: true,
                position: 'right'
              },
              force: {
                repulsion: 450,
                edgeLength: [50, 200],
                gravity: 0.2
              },
              emphasis: {
                focus: 'adjacency',
                lineStyle: {
                  width: 10
                }
              },
              scaleLimit: {
                min: 0.4,
                max: 2
              },
              lineStyle: {
                curveness: 0.3
              },
              edgeSymbol: ['none', 'arrow'],
              edgeLabel: {
                show: true,
                formatter: function (x) {
                  return x.data.name;
                },
                textStyle: {
                  fontSize: 8
                }
              }
            }
          ]
        };
        myChart.setOption(option);
      }).catch(error => {
        console.error("Error fetching entity relationships:", error);
      });
    }
  }
}
</script>
