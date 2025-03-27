<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentCountByType } from "@/api/patentSys/patents";

export default {
  name: "BarPatentByType",
  props: {
    chartId: {
      type: String,
      default: "barCountByType"
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getPatentCountByType().then(response => {
        let names = [];
        let values_invention = [];
        let values_utility = [];
        let data = response.data;

        for (let i = 0; i < data.length; i++) {
          if (i < data.length/2) {
            names.push(data[i].name);
            values_invention.push(data[i].value);
          } else {
            values_utility.push(data[i].value);
          }
        }
        
        let myChart = this.$echarts.init(document.getElementById(this.chartId));
        let option = {
          title: {
            text: '专利类型统计',
            subtext: '单位：件',
            left: 'center'
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['发明', '实用新型'],
            left: 'center',
            top: 'bottom'
          },
          toolbox: {
            show: true,
            feature: {
              dataView: {show: true, readOnly: false},
              magicType: {show: true, type: ['line', 'bar']},
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          calculable: true,
          xAxis: [
            {
              type: 'category',
              data: names
            }
          ],
          yAxis: [
            {
              type: 'value'
            }
          ],
          series: [
            {
              name: '发明',
              type: 'bar',
              data: values_invention,
              markPoint: {
                data: [
                  {type: 'max', name: 'Max'},
                  {type: 'min', name: 'Min'}
                ]
              },
              markLine: {
                data: [{type: 'average', name: 'Avg'}]
              }
            },
            {
              name: '实用新型',
              type: 'bar',
              data: values_utility,
              markPoint: {
                data: [
                  {type: 'max', name: 'Max'},
                  {type: 'min', name: 'Min'}
                ]
              },
              markLine: {
                data: [{type: 'average', name: 'Avg'}]
              }
            }
          ]
        };
        myChart.setOption(option);
      }).catch(error => {
        console.error("Error fetching patent count by type:", error);
      });
    }
  }
}
</script>
