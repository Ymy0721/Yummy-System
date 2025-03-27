<template>
  <div>
    <div :id="chartId" style="height: 400px"></div>
  </div>
</template>

<script>
import { getPatentTitleWordCount } from "@/api/patentSys/patents";
import echarts from "echarts";
import 'echarts-wordcloud';

export default {
  name: "WordCloudPatent",
  props: {
    chartId: {
      type: String,
      default: "wordcloudCountByWord"
    }
  },
  mounted() {
    this.initChart();
  },
  methods: {
    initChart() {
      getPatentTitleWordCount().then(response => {
        let data = response.data.slice(0, 300);

        let maskImage = new Image();
        maskImage.src = require('@/assets/images/mask-image.png');
        maskImage.onload = () => {
          let myChart = this.$echarts.init(document.getElementById(this.chartId));
          let option = {
            title: {
              text: '专利标题词云',
              subtext: '单位：件',
              left: 'center'
            },
            tooltip: {
              show: true
            },
            toolbox: {
              show: true,
              feature: {
                dataView: {show: true, readOnly: false},
                restore: {show: true},
                saveAsImage: {show: true}
              }
            },
            series: [
              {
                type: 'wordCloud',
                maskImage: maskImage,
                left: 'center',
                top: 'bottom',
                width: '90%',
                height: '90%',
                right: null,
                bottom: null,
                sizeRange: [10, 50],
                rotationRange: [-90, 90],
                rotationStep: 45,
                gridSize: 2,
                drawOutOfBound: false,
                textStyle: {
                  normal: {
                    fontFamily: 'sans-serif',
                    fontWeight: 'bold',
                    color: function () {
                      return 'rgb(' + [
                        Math.round(Math.random() * 160),
                        Math.round(Math.random() * 160),
                        Math.round(Math.random() * 160)
                      ].join(',') + ')';
                    }
                  },
                  emphasis: {
                    type: 'highlight',
                    color: '#ff0000'
                  }
                },
                data: data,
              }
            ]
          };
          myChart.setOption(option);
        };
      }).catch(error => {
        console.error("Error fetching patent title word count:", error);
      });
    }
  }
}
</script>
