<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentCountByApplicant } from "@/api/patentSys/patents";

export default {
  name: "PiePatentByApplicant",
  props: {
    chartId: {
      type: String,
      default: "barCountByApplicant"
    },
    selectedM: {
      type: Number,
      default: 10
    }
  },
  watch: {
    selectedM() {
      this.initChart();
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getPatentCountByApplicant().then(response => {
        let data = response.data.slice(0, this.selectedM);
        let myChart = this.$echarts.init(document.getElementById(this.chartId));
        let option = {
          title: {
            text: '申请人专利数量统计',
            subtext: `前${this.selectedM}申请人`,
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
              name: '申请人专利数量',
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
      }).catch(error => {
        console.error("Error fetching patent count by applicant:", error);
      });
    }
  }
}
</script>
