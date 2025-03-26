<template>
  <div class="app-container home">
<!--    <el-row :gutter="20">-->
<!--    </el-row>-->
    <el-row :gutter="20">
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <h2><strong>千殇后台管理框架</strong></h2>
        <p>
          本项目基于《基于专利的技术与人才挖掘模型研究》，针对专利数据，对专利特征进行多维分析，识别前沿技术领域，构建创新型技术与人才挖掘模型，实现了专利数据的可视化展示。
        </p>
        <p>项目组长：袁名宇</p>
        <p>项目成员：杜俊瑾 赵嘉睿</p>
        <p>
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-cloudy"
            plain
            @click="goTarget('https://github.com/Ymy0721/Patent-Search-System')"
            >访问github</el-button
          >
          <el-button
            size="mini"
            icon="el-icon-s-home"
            plain
            @click="goTarget('https://github.com/Ymy0721')"
            >访问主页</el-button
          >
        </p>
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-row>
          <el-col :span="12">
            <h2><strong>技术选型</strong></h2>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <h4>后端技术</h4>
            <ul>
              <li>SpringBoot</li>
              <li>Spring Security</li>
              <li>JWT</li>
              <li>MyBatis</li>
              <li>Druid</li>
              <li>Fastjson</li>
              <li>MySQL</li>
              <li>...</li>
            </ul>
          </el-col>
          <el-col :span="6">
            <h4>前端技术</h4>
            <ul>
              <li>Vue</li>
              <li>Vuex</li>
              <li>Element-ui</li>
              <li>Axios</li>
              <li>Sass</li>
              <li>Quill</li>
              <li>Echarts</li>
              <li>...</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <div id="lineCountByTime" style="height: 400px"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <div id="mapCountByRegion" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">

<!--      <el-col :xs="24" :sm="24" :md="12" :lg="12">-->
<!--        <el-card class="update-log">-->
<!--          <el-select v-model="selectedN" placeholder="选择前N代理机构">-->
<!--            <el-option v-for="n in [5, 10, 15, 20]" :key="n" :label="`前${n}代理机构`" :value="n"></el-option>-->
<!--          </el-select>-->
<!--          <div id="pieCountByAgency" style="height: 400px;"></div>-->
<!--        </el-card>-->
<!--      </el-col>-->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <el-select v-model="selectedM" placeholder="选择前M申请人">
            <el-option v-for="m in [5, 10, 15, 20]" :key="m" :label="`前${m}申请人`" :value="m"></el-option>
          </el-select>
          <div id="barCountByApplicant" style="height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <el-select v-model="selectedP" placeholder="选择前P代理机构">
            <el-option v-for="p in [5, 10, 15, 20]" :key="p" :label="`前${p}发明人`" :value="p"></el-option>
          </el-select>
          <div id="barCountByInventor" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-divider />

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <div id="barCountByType" style="height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <div id="wordcloudCountByWord" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-divider />

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="24">
        <el-card class="update-log">
          <el-select v-model="selectedK" placeholder="选择关系数前K实体">
            <el-option v-for="k in [15, 30, 50, 100]" :key="k" :label="`前${k}实体`" :value="k"></el-option>
          </el-select>
          <div id="entityRelationshipGraph" style="height: 600px;"></div>
        </el-card>
      </el-col>
<!--      <el-col :xs="24" :sm="24" :md="12" :lg="12">-->
<!--        <el-card class="update-log">-->
<!--          <div id="barCountByType" style="height: 435px;"></div>-->
<!--        </el-card>-->
<!--      </el-col>-->
    </el-row>
    <el-loading v-if="loading" fullscreen lock text="Loading..."></el-loading>
  </div>
</template>

<script>
import {getPatentCountByAgency, getPatentCountByRegion, getPatentCountByTime, getPatentCountByApplicant, getPatentCountByInventor, getPatentCountByType, getPatentTitleWordCount, getEntitiesWithRelationship } from "@/api/patentSys/patents";

import echarts from "echarts";
import 'echarts-wordcloud';
import 'echarts/lib/chart/map';
import 'echarts/map/js/china'

export default {
  data() {
    return {
      selectedN: 10,
      selectedM: 10,
      selectedP: 10,
      selectedK: 30,
      loading: false // 添加 loading 属性
    };
  },
  watch: {
    selectedN() {
      this.showPieCountByAgency();
    },
    selectedM() {
      this.showBarCountByApplicant();
    },
    selectedP() {
      this.showBarCountByInventor();
    },
    selectedK() {
      this.showEntityRelationshipGraph();
    }
  },
  mounted() {
    this.showLineCountByTime();
    this.showMapCountByRegion();
    this.showPieCountByAgency();
    this.showBarCountByApplicant();
    this.showBarCountByInventor();
    this.showBarCountByType();
    this.showWordcloudCountByWord();
    this.showEntityRelationshipGraph();
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
    showLineCountByTime() {
      getPatentCountByTime().then(response => {
        let names = [];
        let values = [];
        let data = response.data;
        for (let i = 0; i < data.length; i++) {
          names.push(data[i].name);
          values.push(data[i].value);
        }
        let myChart = this.$echarts.init(document.getElementById('lineCountByTime'));
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
        console.error("Error fetching patent count by agency:", error);
      });
    },
    showMapCountByRegion() {
      getPatentCountByRegion().then(response => {
        let names = [];
        let values = [];
        let data = response.data;
        data.sort((a, b) => a.value - b.value);
        for (let i = 0; i < data.length; i++) {
          names.push(data[i].name);
          values.push(data[i].value);
        }
        let myChart = this.$echarts.init(document.getElementById('mapCountByRegion'));
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
    },
    showPieCountByAgency() {
      this.loading = true; // 开始加载
      getPatentCountByAgency().then(response => {
        let data = response.data.slice(0, this.selectedN);
        // 确保DOM元素存在
        let chartElement = document.getElementById('pieCountByAgency');
        if (!chartElement) {
          console.warn('无法找到图表容器: pieCountByAgency');
          this.loading = false;
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
        this.loading = false; // 加载完成
      }).catch(error => {
        console.error("Error fetching patent count by agency:", error);
        this.loading = false; // 出错也要关闭加载
      });
    },
    showBarCountByApplicant() {
      getPatentCountByApplicant().then(response => {
        let names = [];
        let values = [];
        let data = response.data.slice(0, this.selectedM);
        for (let i = 0; i < data.length; i++) {
          names.push(data[i].name);
          values.push(data[i].value);
        }
        let dataShadow = [];
        for (let i = 0; i < data.length; i++) {
          dataShadow.push(Math.max(...values)*1.2);
        }
        let myChart = this.$echarts.init(document.getElementById('barCountByApplicant'));
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
        console.error("Error fetching patent count by agency:", error);
      });
    },
    showBarCountByInventor() {
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
        let myChart = this.$echarts.init(document.getElementById('barCountByInventor'));
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
          console.log(names[Math.max(params.dataIndex - zoomSize / 2, 0)]);
          myChart.dispatchAction({
            type: 'dataZoom',
            startValue: names[Math.max(params.dataIndex - zoomSize / 2, 0)],
            endValue:
              names[Math.min(params.dataIndex + zoomSize / 2, data.length - 1)]
          });
        });
        myChart.setOption(option);
      }).catch(error => {
        console.error("Error fetching patent count by agency:", error);
      });
    },
    showBarCountByType() {
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
        let myChart = this.$echarts.init(document.getElementById('barCountByType'));
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
              // prettier-ignore
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
        console.error("Error fetching patent count by agency:", error);
      });
    },
    showWordcloudCountByWord() {
      getPatentTitleWordCount().then(response => {
        let data = response.data.slice(0, 300);

        let maskImage = new Image();
        maskImage.src = require('@/assets/images/mask-image.png');
        maskImage.onload = function() {
          let myChart = echarts.init(document.getElementById('wordcloudCountByWord'));
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
        console.error("Error fetching patent count by agency:", error);
      });
    },
    showEntityRelationshipGraph() {
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
          links.push({ source: item.entity1, target: item.entity2, name: item.relation, lineStyle: { color: relationColors[item.relation] } });
          relationTypes.add(item.relation);
        });

        const nodeArray = Array.from(nodes.values());
        const categoryArray = Array.from(categories).map(type => ({ name: type, itemStyle: { color: typeColors[type] } }));
        // const relationArray = Array.from(relationTypes).map(relation => ({ name: relation, icon: 'circle' }));

        const relationArray = Array.from(relationTypes).map(relation => ({
          name: relation,
          itemStyle: { color: relationColors[relation] }
        }));

        const myChart = echarts.init(document.getElementById('entityRelationshipGraph'));
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
                  fontSize: 8 // Adjust the font size as needed
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

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

