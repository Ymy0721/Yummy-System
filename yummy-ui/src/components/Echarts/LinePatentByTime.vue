<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentCountByTime } from "@/api/patentSys/patents";
import echarts from "echarts";

export default {
  name: "LinePatentByTime",
  props: {
    chartId: {
      type: String,
      default: "lineCountByTime"
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getPatentCountByTime().then(response => {
        let names = [];
        let values = [];
        let data = response.data;
        for (let i = 0; i < data.length; i++) {
          names.push(data[i].name);
          values.push(data[i].value);
        }
        let myChart = this.$echarts.init(document.getElementById(this.chartId));
        let totalDataPoints = data.length;
        let startPercentage = (totalDataPoints - totalDataPoints * 0.4) / totalDataPoints * 100;
        let endPercentage = 100;

        let option = {
          tooltip: {
            trigger: 'axis',
            position: function (pt) {
              return [pt[0], '10%'];
            }
          },
          title: {
            left: 'center',
            text: '专利数量按时间统计',
            subtext: '单位：件'
          },
          toolbox: {
            feature: {
              dataZoom: {
                yAxisIndex: 'none'
              },
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: names
          },
          yAxis: {
            type: 'value',
          },
          dataZoom: [
            {
              type: 'inside',
              start: startPercentage,
              end: endPercentage
            },
            {
              start: startPercentage,
              end: endPercentage
            }
          ],
          series: [
            {
              name: '专利数',
              type: 'line',
              symbol: 'none',
              sampling: 'lttb',
              itemStyle: {
                color: 'rgb(255, 70, 131)'
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgb(255, 158, 68)'
                  },
                  {
                    offset: 1,
                    color: 'rgb(255, 70, 131)'
                  }
                ])
              },
              data: values
            }
          ]
        };
        myChart.setOption(option);
      }).catch(error => {
        console.error("Error fetching patent count by time:", error);
      });
    }
  }
}
</script>
