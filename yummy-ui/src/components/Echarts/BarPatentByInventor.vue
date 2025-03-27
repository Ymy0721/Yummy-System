<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentCountByInventor } from "@/api/patentSys/patents";
import echarts from "echarts";

export default {
  name: "BarPatentByInventor",
  props: {
    chartId: {
      type: String,
      default: "barCountByInventor"
    },
    selectedP: {
      type: Number,
      default: 10
    }
  },
  watch: {
    selectedP() {
      this.initChart();
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getPatentCountByInventor().then(response => {
        let names = [];
        let values = [];
        let data = response.data.slice(0, this.selectedP);
        for (let i = 0; i < data.length; i++) {
          names.push(data[i].name);
          values.push(data[i].value);
        }
        let dataShadow = [];
        for (let i = 0; i < data.length; i++) {
          dataShadow.push(Math.max(...values));
        }
        let myChart = this.$echarts.init(document.getElementById(this.chartId));
        let option = {
          title: {
            text: '发明人专利数量统计',
            subtext: `前${this.selectedP}发明人`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c}'
          },
          toolbox: {
            show: true,
            feature: {
              mark: {show: true},
              dataView: {show: true, readOnly: false},
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          xAxis: {
            data: names,
            axisLabel: {
              inside: true,
              color: '#fff'
            },
            axisTick: {
              show: false
            },
            axisLine: {
              show: false
            },
            z: 10
          },
          yAxis: {
            axisLine: {
              show: false
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              color: '#999'
            }
          },
          dataZoom: [
            {
              type: 'inside'
            }
          ],
          series: [
            {
              name: '发明人专利数量',
              type: 'bar',
              showBackground: true,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: 'rgb(255, 158, 68)' },
                  { offset: 0.5, color: 'rgb(255, 70, 131)' },
                  { offset: 0.8, color: 'rgb(255, 70, 131)' }
                ]),
                barBorderRadius: [50, 50, 0, 0]
              },
              emphasis: {
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgb(255, 200, 90)' },
                    { offset: 0.7, color: 'rgb(255, 90, 150)' },
                    { offset: 1, color: 'rgb(255, 90, 150)' }
                  ])
                }
              },
              data: values
            }
          ]
        };
        
        // Enable data zoom when user click bar.
        const zoomSize = 6;
        myChart.on('click', function (params) {
          myChart.dispatchAction({
            type: 'dataZoom',
            startValue: names[Math.max(params.dataIndex - zoomSize / 2, 0)],
            endValue: names[Math.min(params.dataIndex + zoomSize / 2, data.length - 1)]
          });
        });
        myChart.setOption(option);
      }).catch(error => {
        console.error("Error fetching patent count by inventor:", error);
      });
    }
  }
}
</script>
