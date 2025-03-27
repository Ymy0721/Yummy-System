<template>
  <div class="app-container home">
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
            @click="goTarget('https://github.com/Ymy0721/Yummy-System')"
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
          <line-patent-by-time chart-id="lineCountByTime" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <map-patent-by-region chart-id="mapCountByRegion" />
        </el-card>
      </el-col>
    </el-row>
    <el-divider />
    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <el-select v-model="selectedM" placeholder="选择前M申请人">
            <el-option v-for="m in [5, 10, 15, 20]" :key="m" :label="`前${m}申请人`" :value="m"></el-option>
          </el-select>
          <pie-patent-by-applicant chart-id="barCountByApplicant" :selected-m="selectedM" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <el-select v-model="selectedP" placeholder="选择前P发明人">
            <el-option v-for="p in [5, 10, 15, 20]" :key="p" :label="`前${p}发明人`" :value="p"></el-option>
          </el-select>
          <bar-patent-by-inventor chart-id="barCountByInventor" :selected-p="selectedP" />
        </el-card>
      </el-col>
    </el-row>
    <el-divider />

    <el-row :gutter="20">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <bar-patent-by-type chart-id="barCountByType" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="update-log">
          <word-cloud-patent chart-id="wordcloudCountByWord" />
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
          <entity-relationship-graph chart-id="entityRelationshipGraph" :selected-k="selectedK" />
        </el-card>
      </el-col>
    </el-row>
    <el-loading v-if="loading" fullscreen lock text="Loading..."></el-loading>
  </div>
</template>

<script>
import LinePatentByTime from "@/components/Echarts/LinePatentByTime";
import MapPatentByRegion from "@/components/Echarts/MapPatentByRegion";
import PiePatentByApplicant from "@/components/Echarts/PiePatentByApplicant";
import BarPatentByInventor from "@/components/Echarts/BarPatentByInventor";
import BarPatentByType from "@/components/Echarts/BarPatentByType";
import WordCloudPatent from "@/components/Echarts/WordCloudPatent";
import EntityRelationshipGraph from "@/components/Echarts/EntityRelationshipGraph";

export default {
  components: {
    LinePatentByTime,
    MapPatentByRegion,
    PiePatentByApplicant,
    BarPatentByInventor, 
    BarPatentByType,
    WordCloudPatent,
    EntityRelationshipGraph
  },
  data() {
    return {
      selectedN: 10,
      selectedM: 10,
      selectedP: 10,
      selectedK: 30,
      loading: false
    };
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
    setLoading(status) {
      this.loading = status;
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

