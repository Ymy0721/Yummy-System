<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentCountByRegion } from "@/api/patentSys/patents";
import echarts from "echarts";
import 'echarts/lib/chart/map';
import 'echarts/map/js/china';

export default {
  name: "MapPatentByRegion",
  props: {
    chartId: {
      type: String,
      default: "mapCountByRegion"
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getPatentCountByRegion().then(response => {
        let names = [];
        let values = [];
        let data = response.data;
        data.sort((a, b) => a.value - b.value);
        for (let i = 0; i < data.length; i++) {
          names.push(data[i].name);
          values.push(data[i].value);
        }
        
        let myChart = this.$echarts.init(document.getElementById(this.chartId));
        let mapOption = {
          title: {
            text: '专利数量按地区统计',
            subtext: '单位：件',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c}'
          },
          visualMap: {
            min: 0,
            max: 2000,
            left: 'left',
            top: 'bottom',
            text: ['高', '低'],
            calculable: true
          },
          toolbox: {
            show: true,
            orient: 'vertical',
            left: 'right',
            top: 'center',
            feature: {
              dataView: {readOnly: false},
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          series: [
            {
              name: '专利数量',
              type: 'map',
              mapType: 'china',
              roam: false,
              label: {
                show: true,
                color: 'rgb(249, 249, 249)'
              },
              animationDurationUpdate: 2000,
              data: data,
              universalTransition: true
            },
          ]
        };
        
        let barOption = {
          title: {
            text: '专利数量按地区统计',
            subtext: '单位：件',
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c}'
          },
          toolbox: {
            show: true,
            orient: 'vertical',
            left: 'right',
            top: 'center',
            feature: {
              dataView: {readOnly: false},
              restore: {show: true},
              saveAsImage: {show: true}
            }
          },
          xAxis: {
            type: 'value',
            data: values
          },
          yAxis: {
            type: 'category',
            data: names
          },
          animationDurationUpdate: 2000,
          series: {
            type: 'bar',
            data: values,
            universalTransition: true
          }
        };
        
        let currentOption = barOption;
        myChart.setOption(barOption);
        setInterval(function () {
          currentOption = currentOption === mapOption ? barOption : mapOption;
          myChart.setOption(currentOption, true);
        }, 2000);
      }).catch(error => {
        console.error("Error fetching patent count by region:", error);
      });
    }
  }
}
</script>
