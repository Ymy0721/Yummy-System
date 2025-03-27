<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentCountByAgency } from "@/api/patentSys/patents";

export default {
  name: "PiePatentByAgency",
  props: {
    chartId: {
      type: String,
      default: "pieCountByAgency"
    },
    selectedN: {
      type: Number,
      default: 10
    }
  },
  watch: {
    selectedN() {
      this.initChart();
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      this.$emit('loading', true);
      getPatentCountByAgency().then(response => {
        let data = response.data.slice(0, this.selectedN);
        let chartElement = document.getElementById(this.chartId);
        if (!chartElement) {
          console.warn('无法找到图表容器: ' + this.chartId);
          this.$emit('loading', false);
          return;
        }
        let myChart = this.$echarts.init(chartElement);
        let option = {
          title: {
            text: '代理机构专利数量统计',
            subtext: `前${this.selectedN}代理机构`,
            left: 'center'
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)'
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
          series: [
            {
              name: '代理机构专利数量',
              type: 'pie',
              radius: [20, 140],
              center: ['50%', '50%'],
              roseType: 'radius',
              data: data,
              label: {
                show: true
              },
              labelLine: {
                show: true
              },
            }
          ]
        };
        myChart.setOption(option);
        this.$emit('loading', false);
      }).catch(error => {
        console.error("Error fetching patent count by agency:", error);
        this.$emit('loading', false);
      });
    }
  }
}
</script>
