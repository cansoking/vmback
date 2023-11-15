package com.xw.libvirt.controller;

import com.xw.libvirt.pojo.*;
import com.xw.libvirt.service.LibvirtService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
public class LibvirtController {

    @Resource(name = "libvirtService")
    private LibvirtService libvirtService;

    @RequestMapping(value = {"/index"})
    public String index(Model model) {
        Host hostInfo = libvirtService.getHostInfo();
        System.out.println(hostInfo);
        model.addAttribute("hostinfo", hostInfo);
        LibvirtConnect connectInformation = libvirtService.getLibvirtConnectInformation();
        model.addAttribute("connectInformation", connectInformation);
        return "index";
    }

    @RequestMapping("/main")
    public String main(Model model) {
        List<Virtual> virtualList = libvirtService.getVirtualList();
        model.addAttribute("virtualList", virtualList);
        String netState = libvirtService.getNetState();
        model.addAttribute("netState", netState);
        return "main";

    }

    // 新增，获取虚拟机列表
    @ResponseBody
    @GetMapping("/getVMList")
    public List<Virtual> getVMList() {
        return libvirtService.getVirtualList();
    }

    @RequestMapping("/openOrCloseNetWork")
    public String openOrCloseNetWork(@RequestParam("netState") String netState) {
        if ("on".equals(netState)) libvirtService.closeNetWork();
        if ("off".equals(netState)) libvirtService.openNetWork();
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/initiate")
    public String initiateVirtual(@RequestParam("name") String name) {
        libvirtService.initiateDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/suspended")
    public String suspendedVirtual(@RequestParam("name") String name) {
        libvirtService.suspendedDomainName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/resume")
    public String resumeVirtual(@RequestParam("name") String name) {
        libvirtService.resumeDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/save")
    public String saveVirtual(@RequestParam("name") String name) {
        libvirtService.saveDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/restore")
    public String restoreVirtual(@RequestParam("name") String name) {
        libvirtService.restoreDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/shutdown")
    public String shutdownVirtual(@RequestParam("name") String name) {
        libvirtService.shutdownDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/shutdownMust")
    public String shutdownMustVirtual(@RequestParam("name") String name) {
        libvirtService.shutdownMustDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @SneakyThrows
    @RequestMapping("/reboot")
    public String rebootVirtual(@RequestParam("name") String name) {
        libvirtService.rebootDomainByName(name);
        Thread.sleep(1000);
        return "redirect:main";
    }

    @RequestMapping("/delete")
    public String deleteVirtual(@RequestParam("name") String name) {
        libvirtService.deleteDomainByName(name);
        libvirtService.deleteImgFile(name + ".qcow2");
        return "redirect:main";
    }

    @RequestMapping("/toAddVirtual")
    public String toAddVirtual(Model model) {
        String netState = libvirtService.getNetState();
        model.addAttribute("netState", netState);
        return "addVirtual";
    }

    @ResponseBody
    @PostMapping(value = "/addVirtual")
    public String addVirtual(@RequestParam("file") MultipartFile file, @RequestParam("name") String name, @RequestParam("memory") String memory, @RequestParam("cpuNum") String cpuNum, @RequestParam("OStype") String OStype) {
        VM_create vmc = new VM_create();
        vmc.setName(name);
        vmc.setMemory(Long.parseLong(memory));
        vmc.setCpuNum(Integer.parseInt(cpuNum));
        vmc.setOStype(OStype);
        libvirtService.addDomainByName(vmc);
        libvirtService.addImgFile(vmc.getName(), file);
        return "yes";
    }

    @RequestMapping("/img")
    public String imgList(Model model) {
        List<ImgFile> imgList = libvirtService.getImgList();
        model.addAttribute("imgList", imgList);
        String netState = libvirtService.getNetState();
        model.addAttribute("netState", netState);
        return "img";
    }

    @RequestMapping("/toAddImg")
    public String toAddImg(Model model) {
        String netState = libvirtService.getNetState();
        model.addAttribute("netState", netState);
        return "addImg";
    }

    @RequestMapping("/addImg")
    public String addImg(@RequestParam("imgName") String name,
                         @RequestPart("file") MultipartFile file) {
        libvirtService.addImgFile(name, file);
        return "redirect:img";
    }

    @RequestMapping("/deleteImg")
    public String deleteImg(@RequestParam("name") String name) {
        libvirtService.deleteImgFile(name);
        return "redirect:img";
    }

    @ResponseBody
    @RequestMapping("/downImg")
    public String downImg(@RequestParam("name") String name, HttpServletResponse response) {
        return libvirtService.downImgFile(name, response);
    }

    @RequestMapping("/getSnapshotList")
    public String getSnapshotList(@RequestParam("name") String name,
                                  Model model) {
        List<Snapshot> snapshotList = libvirtService.getSnapshotListByName(name);
        model.addAttribute("snapshotList", snapshotList);
        model.addAttribute("virtualName", name);
        return "snapshot";
    }

    @SneakyThrows
    @RequestMapping("/deleteSnapshot")
    public String deleteSnapshot(@RequestParam("virtualName") String virtualName,
                                 @RequestParam("snapshotName") String snapshotName) {
        libvirtService.deleteSnapshot(virtualName, snapshotName);
        Thread.sleep(1000);
        return "redirect:/getSnapshotList?name=" + virtualName;
    }

    @SneakyThrows
    @RequestMapping("/revertSnapshot")
    public String revertSnapshot(@RequestParam("virtualName") String virtualName,
                                 @RequestParam("snapshotName") String snapshotName) {
        libvirtService.revertSnapshot(virtualName, snapshotName);
        Thread.sleep(1000);
        return "redirect:/getSnapshotList?name=" + virtualName;
    }

    @SneakyThrows
    @RequestMapping("/createSnapshot")
    public String createSnapshot(@RequestParam("virtualName") String virtualName,
                                 @RequestParam("snapshotName") String snapshotName) {
        libvirtService.createSnapshot(virtualName, snapshotName);
        Thread.sleep(1000);
        return "redirect:/getSnapshotList?name=" + virtualName;
    }

    @RequestMapping("/storagePoolList")
    public String storagePoolList(Model model) {
        List<Storagepool> storagePoolList = libvirtService.getStoragePoolList();
        model.addAttribute("storagePoolList", storagePoolList);
        String netState = libvirtService.getNetState();
        model.addAttribute("netState", netState);
        return "storagepool";
    }

    @SneakyThrows
    @RequestMapping("/deleteStoragePool")
    public String deleteStoragePool(@RequestParam("name") String name) {
        libvirtService.deleteStoragePool(name);
        Thread.sleep(1000);
        return "redirect:/storagePoolList";
    }

    @RequestMapping("/toCreateStoragepool")
    public String toCreateStoragepool(Model model) {
        String netState = libvirtService.getNetState();
        model.addAttribute("netState", netState);
        return "addStoragepool";
    }

    @SneakyThrows
    @RequestMapping("/createStoragepool")
    public String createStoragepool(@RequestParam("storagepoolName") String name,
                                    @RequestParam("storagepoolPath") String url) {
        libvirtService.createStoragepool(name, url);
        Thread.sleep(1000);
        return "redirect:/storagePoolList";
    }

}
