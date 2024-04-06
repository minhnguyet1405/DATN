<template>
  <div v-show="value" class="vue-image-crop-upload">
    <div class="vicp-wrap">
      <div class="vicp-crop">
        <div class="vicp-crop-title">{{ title }}</div>
        <div v-show="true" class="vicp-crop-content">
          <div class="vicp-img-bacground">
            <img
              ref="img"
              :src="sourceImgUrl"
              :style="sourceImgBackgroundStyle"
            >
          </div>
          <div class="vicp-img-container">
            <img
              ref="img"
              :src="sourceImgUrl"
              :style="sourceImgStyle"
              class="vicp-img"
              draggable="false"
              @drag="preventDefault"
              @dragstart="preventDefault"
              @dragend="preventDefault"
              @dragleave="preventDefault"
              @dragover="preventDefault"
              @dragenter="preventDefault"
              @drop="preventDefault"
              @touchstart="imgStartMove"
              @touchmove="imgMove"
              @touchend="createImg"
              @touchcancel="createImg"
              @mousedown="imgStartMove"
              @mousemove="imgMove"
              @mouseup="createImg"
              @mouseout="createImg"
            >
          </div>
        </div>
      </div>
      <div class="vicp-range">
        <img src="@/assets/images/decrease.png" class="scale-control scale-down" @mousedown="startZoomSub" @mouseout="endZoomSub" @mouseup="endZoomSub">
        <el-slider v-model="scale.range" :step="1" :min="0" :max="100" :show-tooltip="false" @input="zoomChange" />
        <img src="@/assets/images/increase.png" class="scale-control scale-up" @mousedown="startZoomAdd" @mouseout="endZoomAdd" @mouseup="endZoomAdd">
        <i class="vicp-icon5" @mousedown="startZoomSub" @mouseout="endZoomSub" @mouseup="endZoomSub" />
        <i class="vicp-icon6" @mousedown="startZoomAdd" @mouseout="endZoomAdd" @mouseup="endZoomAdd" />
      </div>
      <div class="vicp-operate">
        <a class="vicp-operate-btn btn-off" @click="off" @mousedown="ripple">{{ lang.btn.off }}</a>
        <a class="vicp-operate-btn btn-save" @click="prepareUpload" @mousedown="ripple">{{ lang.btn.save }}</a>
      </div>
      <canvas v-show="false" ref="canvas" :width="width" :height="height" />
    </div>
  </div>
</template>

<script>
/* eslint-disable */
'use strict';
import axios from 'axios';
import language from './utils/language.js';
import mimes from './utils/mimes.js';
import data2blob from './utils/data2blob.js';
import effectRipple from './utils/effect-ripple.js';
import { isLogged } from '@/utils/auth'

export default {
  props: {
    title: {
      type: String,
      'default': '',
    },
    file: {
      type: Object,
      'default': null,
    },
    // Upload field name
    field: {
      type: String,
      'default': 'avatar',
    },
    // Identify
    ki: {
      'default': 0,
    },
    // Show the control or stept
    value: {
      'default': true,
    },
    // URL to upload
    url: {
      type: String,
      'default': '',
    },
    // Other data to be uploaded along with file, as in object format
    params: {
      type: Object,
      'default': null,
    },
    // Add custom headers
    headers: {
      type: Object,
      'default': null,
    },
    // Width to crop
    width: {
      type: Number,
      default: 200,
    },
    // Height to crop
    height: {
      type: Number,
      default: 200,
    },
    // Max-size of single file
    maxSize: {
      type: Number,
      'default': 10240,
    },
    // Language type
    langType: {
      type: String,
      'default': 'en',
    },
    // Language pack
    langExt: {
      type: Object,
      'default': null,
    },
    // Image format
    imgFormat: {
      type: String,
      'default': 'png',
    },
    // For cross-domain support, credentials will be sent
    withCredentials: {
      type: Boolean,
      'default': false,
    },
  },
  data() {
    const that = this;
    const {
      imgFormat,
      langType,
      langExt,
      width,
      height,
    } = that;
    let isSupported = true;
    const allowedImgFormat = [
      'jpg',
      'png',
    ];
    const tempImgFormat = allowedImgFormat.indexOf(imgFormat) === -1 ? 'jpg' : imgFormat;
    const lang = language[langType] ? language[langType] : language['en'];
    const mime = mimes[tempImgFormat];
    // Specification image format
    that.imgFormat = tempImgFormat;
    if (langExt) {
      Object.assign(lang, langExt);
    }
    if (typeof FormData !== 'function') {
      isSupported = false;
    }

    return {
      // Mime image
      mime,
      // Language pack
      lang,
      // Check if browser supports this control
      isSupported,
      // Does the browser supports touch event?
      isSupportTouch: document.hasOwnProperty('ontouchstart'),
      // Upload status and progress
      loading: 0, // 0 - stept started, 1 - is being loaded, 2 - success, 3 - error
      progress: 0,
      // Is the any error?
      hasError: false,
      errorMsg: '',
      // Aspect ratio
      ratio: width / height,
      // Original image URL
      sourceImg: null,
      sourceImgUrl: '',
      // Generated image URL
      createImgUrl: '',
      // Image drag event initial value
      sourceImgMouseDown: {
        on: false,
        mX: 0, // Mouse pressed coordinates
        mY: 0,
        x: 0, // scale original coordinates
        y: 0,
      },
      // Generate container size for image preview
      previewContainer: {
        width: 100,
        height: 100,
      },
      // Original container size
      sourceImgBackgroundContainer: { // sic
        width: 400,
        height: 200, // If the generated graph scale is consistent with this, there will be a bug, first change to a special format
      },
      // Original container size
      sourceImgContainer: { // sic
        width: 200,
        height: 200, // If the generated graph scale is consistent with this, there will be a bug, first change to a special format
      },
      // Original image display attribute
      scale: {
        zoomAddOn: false, // Button zoom event is on/off
        zoomSubOn: false, // Button zoom event is on/off
        range: 1, // Up to 100
        rotateLeft: false, // Button left rotation event turned on
        rotateRight: false, // Button right rotation event turned on
        degree: 0, // Degree of rotation
        x: 0,
        y: 0,
        width: 0,
        height: 0,
        maxWidth: 0,
        maxHeight: 0,
        minWidth: 0, // Min width
        minHeight: 0,
        naturalWidth: 0, // Original width
        naturalHeight: 0,
      },
    };
  },
  computed: {
    // Progress bar
    progressStyle() {
      const {
        progress,
      } = this;

      return {
        width: progress + '%',
      };
    },
    sourceImgBackgroundStyle() {
      const {
        scale,
        sourceImgBackgroundMasking,
      } = this;
      const top = scale.y + sourceImgBackgroundMasking.y + 'px';
      const left = scale.x + sourceImgBackgroundMasking.x + 'px';
      return {
        top,
        left,
        width: scale.width + 'px',
        height: scale.height + 'px',
        transform: 'rotate(' + scale.degree + 'deg)', // When rotating, the original image rotation style on the left side
        '-ms-transform': 'rotate(' + scale.degree + 'deg)', // Compatible with IE9
        '-moz-transform': 'rotate(' + scale.degree + 'deg)', // Compatible with FireFox
        '-webkit-transform': 'rotate(' + scale.degree + 'deg)', // Safari and Chrome
        '-o-transform': 'rotate(' + scale.degree + 'deg)', // Opera
      };
    },
    sourceImgBackgroundMasking() {
      const {
        width,
        height,
        ratio,
        sourceImgBackgroundContainer,
      } = this;
      const sic = sourceImgBackgroundContainer;
      const sicRatio = sic.width / sic.height; // Original container aspect ratio
      let x = 0;
      let y = 0;
      let w = sic.width;
      let h = sic.height;
      let scale = 1;
      if (ratio < sicRatio) {
        scale = sic.height / height;
        w = sic.height * ratio;
        x = (sic.width - w) / 2;
      }
      if (ratio > sicRatio) {
        scale = sic.width / width;
        h = sic.width / ratio;
        y = (sic.height - h) / 2;
      }
      return {
        scale, // The mask is relatively wide and the demand is zoomed
        x,
        y,
        width: w,
        height: h,
      };
    },
    // Original image
    sourceImgStyle() {
      const {
        scale,
        sourceImgMasking,
      } = this;
      const top = scale.y + sourceImgMasking.y + 'px';
      const left = scale.x + sourceImgMasking.x + 'px';
      return {
        top,
        left,
        width: scale.width + 'px',
        height: scale.height + 'px',
        transform: 'rotate(' + scale.degree + 'deg)', // When rotating, the original image rotation style on the left side
        '-ms-transform': 'rotate(' + scale.degree + 'deg)', // Compatible with IE9
        '-moz-transform': 'rotate(' + scale.degree + 'deg)', // Compatible with FireFox
        '-webkit-transform': 'rotate(' + scale.degree + 'deg)', // Safari and Chrome
        '-o-transform': 'rotate(' + scale.degree + 'deg)', // Opera
      };
    },
    // Original mark attribute
    sourceImgMasking() {
      const {
        width,
        height,
        ratio,
        sourceImgContainer,
      } = this;
      const sic = sourceImgContainer;
      const sicRatio = sic.width / sic.height; // Original container aspect ratio
      let x = 0;
      let y = 0;
      let w = sic.width;
      let h = sic.height;
      let scale = 1;
      if (ratio < sicRatio) {
        scale = sic.height / height;
        w = sic.height * ratio;
        x = (sic.width - w) / 2;
      }
      if (ratio > sicRatio) {
        scale = sic.width / width;
        h = sic.width / ratio;
        y = (sic.height - h) / 2;
      }
      return {
        scale, // The mask is relatively wide and the demand is zoomed
        x,
        y,
        width: w,
        height: h,
      };
    },
    // Original mark style
    sourceImgShadeStyle() {
      const {
        sourceImgMasking,
        sourceImgContainer,
      } = this;
      const sic = sourceImgContainer;
      const sim = sourceImgMasking;
      const w = sim.width === sic.width ? sim.width : (sic.width - sim.width) / 2;
      const h = sim.height === sic.height ? sim.height : (sic.height - sim.height) / 2;
      return {
        width: w + 'px',
        height: h + 'px',
      };
    },
    previewStyle() {
      const {
        width,
        height,
        ratio,
        previewContainer,
      } = this;
      const pc = previewContainer;
      let w = pc.width;
      let h = pc.height;
      const pcRatio = w / h;
      if (ratio < pcRatio) {
        w = pc.height * ratio;
      }
      if (ratio > pcRatio) {
        h = pc.width / ratio;
      }
      return {
        width: w + 'px',
        height: h + 'px',
      };
    },
  },
  watch: {
    value(newValue) {
      if (newValue && this.loading !== 1) {
        this.reset();
      }
    },
    file: {
      handler: function(newValue) {
        if (newValue.data) {
          this.handleChange(newValue.data)
        }
      },
      deep: true
    }
  },
  methods: {
    // Click ripple effect
    ripple(e) {
      effectRipple(e);
    },
    // Close control
    off() {
      setTimeout(() => {
        this.$emit('input', false);
        this.$emit('close');
      }, 200);
    },
    /* Picture selection area function binding
     ---------------------------------------------------------------*/
    preventDefault(e) {
      e.preventDefault();
      return false;
    },
    handleChange(file) {
      this.reset();
      if (this.checkFile(file)) {
        this.setSourceImg(file);
      }
    },
    /* ---------------------------------------------------------------*/
    // Check if the selected file is suitable
    checkFile(file) {
      const that = this;
      const {
        lang,
        maxSize,
      } = that;
      // Image only
      if (file.type.indexOf('image') === -1) {
        that.hasError = true;
        that.errorMsg = lang.error.onlyImg;
        return false;
      }
      // Check file size
      if (file.size / 1024 > maxSize) {
        that.hasError = true;
        that.errorMsg = lang.error.outOfSize + maxSize + 'kb';
        return false;
      }
      return true;
    },
    // Reset control
    reset() {
      const that = this;
      that.loading = 0;
      that.hasError = false;
      that.errorMsg = '';
      that.progress = 0;
    },
    // Set image source
    setSourceImg(file) {
      const that = this;
      const fr = new FileReader();

      fr.onload = function(e) {
        that.sourceImgUrl = fr.result;
        that.startCrop();
      };
      fr.readAsDataURL(file);
    },
    // Preparing before cropping
    startCrop() {
      const that = this;
      const {
        width,
        height,
        ratio,
        scale,
        sourceImgUrl,
        sourceImgMasking,
        lang,
      } = that;
      const sim = sourceImgMasking;
      const img = new Image();
      img.src = sourceImgUrl;
      img.onload = function() {
        const nWidth = img.naturalWidth;
        const nHeight = img.naturalHeight;
        const nRatio = nWidth / nHeight;
        let w = sim.width;
        let h = sim.height;
        let x = 0;
        let y = 0;
        // Image size don't meet the minimum requirement
        if (nWidth < width || nHeight < height) {
          that.hasError = true;
          that.errorMsg = lang.error.lowestPx + width + '*' + height;
          return false;
        }
        if (ratio > nRatio) {
          h = w / nRatio;
          y = (sim.height - h) / 2;
        }
        if (ratio < nRatio) {
          w = h * nRatio;
          x = (sim.width - w) / 2;
        }
        scale.range = 0;
        scale.x = x;
        scale.y = y;
        scale.width = w;
        scale.height = h;
        scale.degree = 0;
        scale.minWidth = w;
        scale.minHeight = h;
        scale.maxWidth = nWidth * sim.scale;
        scale.maxHeight = nHeight * sim.scale;
        scale.naturalWidth = nWidth;
        scale.naturalHeight = nHeight;
        that.sourceImg = img;
        that.createImg();
      };
    },
    // Mouse presses the picture ready to move
    imgStartMove(e) {
      e.preventDefault();
      // Support for touch events, mouse events are invalid
      if (this.isSupportTouch && !e.targetTouches) {
        return false;
      }
      const et = e.targetTouches ? e.targetTouches[0] : e;
      const {
        sourceImgMouseDown,
        scale,
      } = this;
      const simd = sourceImgMouseDown;
      simd.mX = et.screenX;
      simd.mY = et.screenY;
      simd.x = scale.x;
      simd.y = scale.y;
      simd.on = true;
    },
    // Move when the mouse is pressed, the picture moves
    imgMove(e) {
      e.preventDefault();
      // Support for touch events, mouse events are invalid
      if (this.isSupportTouch && !e.targetTouches) {
        return false;
      }
      const et = e.targetTouches ? e.targetTouches[0] : e;
      const {
        sourceImgMouseDown: {
          on,
          mX,
          mY,
          x,
          y,
        },
        scale,
        sourceImgMasking,
      } = this;
      const sim = sourceImgMasking;
      const nX = et.screenX;
      const nY = et.screenY;
      const dX = nX - mX;
      const dY = nY - mY;
      let rX = x + dX;
      let rY = y + dY;
      if (!on) {
        return;
      };
      if (rX > 0) {
        rX = 0;
      }
      if (rY > 0) {
        rY = 0;
      }
      if (rX < sim.width - scale.width) {
        rX = sim.width - scale.width;
      }
      if (rY < sim.height - scale.height) {
        rY = sim.height - scale.height;
      }
      scale.x = rX;
      scale.y = rY;
    },
    // Button press to zoom in
    startZoomAdd(e) {
      const that = this;
      const {
        scale,
      } = that;
      scale.zoomAddOn = true;
      function zoom() {
        if (scale.zoomAddOn) {
          const range = scale.range >= 100 ? 100 : ++scale.range;
          that.zoomImg(range);
          setTimeout(function() {
            zoom();
          }, 60);
        }
      }
      zoom();
    },
    // Button release or remove to cancel zoom in
    endZoomAdd(e) {
      this.scale.zoomAddOn = false;
    },
    // Button press to start zooming out
    startZoomSub(e) {
      const that = this;
      const {
        scale,
      } = that;
      scale.zoomSubOn = true;
      function zoom() {
        if (scale.zoomSubOn) {
          const range = scale.range <= 0 ? 0 : --scale.range;
          that.zoomImg(range);
          setTimeout(function() {
            zoom();
          }, 60);
        }
      }
      zoom();
    },
    // Button release or remove to cancel zoom out
    endZoomSub(e) {
      const {
        scale,
      } = this;
      scale.zoomSubOn = false;
    },
    zoomChange(e) {
      if (this.sourceImg) {
        this.zoomImg(e);
      }
    },
    // Zoom original
    zoomImg(newRange) {
      const that = this;
      const {
        sourceImgMasking,
        sourceImgMouseDown,
        scale,
      } = this;
      const {
        maxWidth,
        maxHeight,
        minWidth,
        minHeight,
        width,
        height,
        x,
        y,
        range,
      } = scale;
      const sim = sourceImgMasking;
      // Mask width
      const sWidth = sim.width;
      const sHeight = sim.height;
      // New width and height
      const nWidth = minWidth + (maxWidth - minWidth) * newRange / 100;
      const nHeight = minHeight + (maxHeight - minHeight) * newRange / 100;
      // New coordinates (scaled according to the center point of the mask)
      let nX = sWidth / 2 - (nWidth / width) * (sWidth / 2 - x);
      let nY = sHeight / 2 - (nHeight / height) * (sHeight / 2 - y);
      // Determine if the new coordinates exceed the mask limit
      if (nX > 0) {
        nX = 0;
      }
      if (nY > 0) {
        nY = 0;
      }
      if (nX < sWidth - nWidth) {
        nX = sWidth - nWidth;
      }
      if (nY < sHeight - nHeight) {
        nY = sHeight - nHeight;
      }
      // Process scaling
      scale.x = nX;
      scale.y = nY;
      scale.width = nWidth;
      scale.height = nHeight;
      scale.range = newRange;
      setTimeout(function() {
        if (scale.range === newRange) {
          that.createImg();
        }
      }, 300);
    },
    // Generate image
    createImg(e) {
      const that = this;
      const {
        mime,
        sourceImg,
        scale: {
          x,
          y,
          width,
          height,
          degree,
        },
        sourceImgMasking: {
          scale,
        },
      } = that;
      const canvas = that.$refs.canvas;
      const ctx = canvas.getContext('2d');
      if (e) {
        // Cancel the mouse to move the state
        that.sourceImgMouseDown.on = false;
      }
      canvas.width = that.width;
      canvas.height = that.height;
      ctx.clearRect(0, 0, that.width, that.height);
      // Set the transparent area to the white bottom
      ctx.fillStyle = '#fff';
      ctx.fillRect(0, 0, that.width, that.height);
      ctx.translate(that.width * 0.5, that.height * 0.5);
      ctx.rotate(Math.PI * degree / 180);
      ctx.translate(-that.width * 0.5, -that.height * 0.5);
      ctx.drawImage(sourceImg, x / scale, y / scale, width / scale, height / scale);
      that.createImgUrl = canvas.toDataURL(mime);
    },
    prepareUpload() {
      const {
        url,
        createImgUrl,
        field,
        ki,
      } = this;
      this.$emit('crop-success', createImgUrl, field, ki);
      if (typeof url === 'string' && url) {
        this.upload();
      } else {
        this.off();
      }
    },
    // Upload image
    upload() {
      const that = this;
      const {
        lang,
        imgFormat,
        mime,
        url,
        params,
        headers,
        field,
        ki,
        createImgUrl,
        withCredentials,
      } = this;
      const fmData = new FormData();
      fmData.append(field, data2blob(createImgUrl, mime), field + '.' + imgFormat);
      // Add other paramaters
      if (typeof params === 'object' && params) {
        Object.keys(params).forEach((k) => {
          fmData.append(k, params[k]);
        });
      }
      // Monitor progress callback
      const uploadProgress = function(event) {
        if (event.lengthComputable) {
          that.progress = 100 * Math.round(event.loaded) / event.total;
        }
      };
      //Upload files
      that.reset();
      that.loading = 1;
      axios.post(process.env.VUE_APP_UPLOAD_URL + url, fmData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': 'Bearer ',
        },
      })
      .then(resData => {
        that.loading = 2;
        that.$emit('crop-upload-success', resData.data);
      }).catch(err => {
        if (that.value) {
          that.loading = 3;
          that.hasError = true;
          that.errorMsg = lang.fail;
          that.$emit('crop-upload-fail', err, field, ki);
        }
      }).finally(() => {
        that.off()
      });
    },
  },
  created() {
    // Binding button esc to hide this plugin event
    document.addEventListener('keyup', (e) => {
      if (this.value && (e.key === 'Escape' || e.keyCode === 27)) {
        this.off();
      }
    });
  },
};
</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.vue-image-crop-upload {
  position: fixed;
  display: block;
  box-sizing: border-box;
  z-index: 10000;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.44);
  -webkit-tap-highlight-color: transparent;
  -moz-tap-highlight-color: transparent;
  .vicp-wrap {
    position: absolute;
    box-sizing: border-box;
    z-index: 10000;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    border-radius: 3px;
    box-shadow: 0 50px 50px 0 rgba(0, 0, 0, 0.1);
    margin: auto;
    width: 400px;
    padding: 25px 0;
    background-color: #fff;
    -webkit-animation: vicp .12s ease-in;
    animation: vicp .12s ease-in;
    .vicp-crop {
      overflow: hidden;
      .vicp-crop-title {
        padding: 0 35px;
        font-size: 1.375rem;
        font-weight: bold;
        line-height: 1.18;
        color: #172948;
        margin-bottom: 30px;
      }
      .vicp-crop-content {
        position: relative;
        display: block;
        width: 100%;
        height: 200px;
        margin: 0 auto;
        .vicp-img-bacground {
          position: absolute;
          width: 100%;
          height: 200px;
          z-index: 9;
          opacity: 0.46;
          overflow: hidden;
          /*left: 0;
          right: 0;
          margin: auto;*/
          img {
            position: absolute;
            /*left: 100px !important;*/
            width: 200px;
            max-width: unset !important;
          }
        }
        .vicp-img-container {
          position: relative;
          display: block;
          width: 200px;
          height: 200px;
          margin: auto;
          border-radius: 50%;
          overflow: hidden;
          z-index: 10;
          .vicp-img {
            position: absolute;
            display: block;
            cursor: move;
            -webkit-user-select: stepne;
            -moz-user-select: stepne;
            -ms-user-select: stepne;
            user-select: stepne;
            max-width: unset !important;
          }
        }
      }
    }
    .vicp-range {
      position: relative;
      margin: 30px 0;
      padding: 0 35px;
      text-align: center;
      .scale-control {
        cursor: pointer;
        &.scale-down {
          margin-top: 2px;
        }
      }
    }
    .vicp-operate {
      text-align: right;
      padding: 0 35px;
      .vicp-operate-btn {
        border-radius: 26px;
        padding: 5px 25px;
        font-size: 0.875rem;
        line-height: 1.19;
        &.btn-off {
          border: solid 1px #8b8d8f;
          background-color: #ffffff;
          color: #8b8d8f;
        }
        &.btn-save {
          border: solid 1px #05b6b9;
          background-color: #05b6b9;
          color: #ffffff;
          margin-left: 15px;
        }
      }
    }
  }
}
</style>
<style rel="stylesheet/scss" lang="scss">
.vicp-range {
  .el-slider {
    width: 200px;
    display: inline-block;
    margin: 0 15px;
  }
  .el-slider__runway {
    height: 5px;
    margin: 0;
    background-color: #d4d4d4;
    border-radius: 4px;
    .el-slider__bar {
      height: 5px;
      border-top-left-radius: 4px;
      border-bottom-left-radius: 4px;
      background-color: #05b6b9;
    }
    .el-slider__button {
      width: 14px;
      height: 14px;
      border: 0;
      background-color: #05b6b9;
    }
  }
}
</style>
